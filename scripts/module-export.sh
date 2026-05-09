#!/usr/bin/env bash
#
# @file module-export.sh
# @description 模組搬移計畫與 rsync 匯出工具 / Module export planning and rsync helper
# @description_en Expands module dependencies and prints or executes reusable component copy steps
# @description_zh 依所選模組展開依賴，輸出跨專案搬移需要的後端、前端與整合檔案清單

set -eo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
CATALOG_FILE="$REPO_ROOT/module/backend/module-system/src/main/resources/module-catalog.tsv"

MODULES_INPUT=""
TARGET_ROOT=""
FORMAT="plan"
EXECUTE="false"

REQUESTED_MODULES=()
REQUIRED_MODULES=()
UNKNOWN_MODULES=()
RESOLVING_MODULES=()
ADDITIONAL_MODULES=()
BACKEND_MODULES=()
FRONTEND_FEATURES=()
FLYWAY_LOCATIONS=()
DEFAULT_PATHS=()
SUPPORT_PATHS=()
COPY_PATHS=()

generated_at() {
  date -u '+%Y-%m-%dT%H:%M:%SZ'
}

git_metadata() {
  local field="$1"

  case "$field" in
    branch)
      git -C "$REPO_ROOT" rev-parse --abbrev-ref HEAD 2>/dev/null || echo "unknown"
      ;;
    commit)
      git -C "$REPO_ROOT" rev-parse HEAD 2>/dev/null || echo "unknown"
      ;;
    short_commit)
      git -C "$REPO_ROOT" rev-parse --short=12 HEAD 2>/dev/null || echo "unknown"
      ;;
    tag)
      git -C "$REPO_ROOT" describe --tags --exact-match 2>/dev/null || echo ""
      ;;
    describe)
      git -C "$REPO_ROOT" describe --tags --always --dirty 2>/dev/null || echo "unknown"
      ;;
  esac
}

git_is_dirty() {
  if ! git -C "$REPO_ROOT" rev-parse --is-inside-work-tree >/dev/null 2>&1; then
    echo "false"
    return
  fi
  if git -C "$REPO_ROOT" diff --quiet --ignore-submodules -- \
    && git -C "$REPO_ROOT" diff --cached --quiet --ignore-submodules --; then
    echo "false"
  else
    echo "true"
  fi
}

usage() {
  cat <<'USAGE'
模組搬移工具 / Module export helper

Usage:
  scripts/module-export.sh --modules payroll,leave
  scripts/module-export.sh --modules payroll --format json
  scripts/module-export.sh --modules payroll --format config
  scripts/module-export.sh --all --format markdown
  scripts/module-export.sh --modules payroll --format rsync --target /path/to/project
  scripts/module-export.sh --modules payroll --target /path/to/project --execute
  scripts/module-export.sh --all
  scripts/module-export.sh --list

Options:
  --modules <list>   Comma or space separated module keys.
  --all              Select all reusable modules.
  --target <path>    Target project root for rsync commands or execution.
  --format <format>  plan, json, config, markdown, or rsync. Default: plan.
  --execute          Execute rsync copy. Requires --target.
  --list             Print known module keys.
  -h, --help         Show this help.

Default mode is dry-run. It prints a reviewable plan and never writes to a target
project unless --execute is provided.
USAGE
}

ensure_catalog_file() {
  if [[ ! -f "$CATALOG_FILE" ]]; then
    echo "Module catalog not found: $CATALOG_FILE" >&2
    exit 1
  fi
}

known_modules() {
  awk -F '\t' 'NF && $1 !~ /^#/ { print $1 }' "$CATALOG_FILE"
}

catalog_field_for() {
  local module="$1"
  local field_index="$2"
  awk -F '\t' -v module="$module" -v field_index="$field_index" '
    NF && $1 !~ /^#/ && $1 == module {
      print $field_index
      found = 1
      exit
    }
    END {
      if (!found) {
        exit 1
      }
    }
  ' "$CATALOG_FILE"
}

is_known_module() {
  catalog_field_for "$1" 1 >/dev/null 2>&1
}

dependencies_for() {
  local dependencies
  dependencies="$(catalog_field_for "$1" 7 || true)"
  if [[ -z "$dependencies" || "$dependencies" == "-" ]]; then
    echo ""
    return
  fi
  printf '%s\n' "$dependencies" | tr ',' ' '
}

display_name_for() {
  local display_name
  local display_name_en
  display_name="$(catalog_field_for "$1" 2 || true)"
  display_name_en="$(catalog_field_for "$1" 3 || true)"
  if [[ -z "$display_name" ]]; then
    echo "$1"
    return
  fi
  echo "$display_name / $display_name_en"
}

default_path_for() {
  local default_path
  default_path="$(catalog_field_for "$1" 6 || true)"
  if [[ "$default_path" == "-" ]]; then
    echo ""
    return
  fi
  echo "$default_path"
}

flyway_location_for() {
  catalog_field_for "$1" 9 || true
}

source_key_for() {
  catalog_field_for "$1" 8 || true
}

phase_for() {
  catalog_field_for "$1" 4 || true
}

priority_for() {
  catalog_field_for "$1" 5 || true
}

append_unique() {
  local array_name="$1"
  local value="$2"
  local existing
  eval "local values=(\"\${${array_name}[@]}\")"
  for existing in "${values[@]}"; do
    if [[ "$existing" == "$value" ]]; then
      return
    fi
  done
  eval "${array_name}+=(\"\$value\")"
}

array_contains() {
  local value="$1"
  shift
  local item
  for item in "$@"; do
    if [[ "$item" == "$value" ]]; then
      return 0
    fi
  done
  return 1
}

normalize_modules() {
  local raw="${1//,/ }"
  local module
  for module in $raw; do
    module="$(printf '%s' "$module" | tr '[:upper:]' '[:lower:]')"
    if [[ -n "$module" ]]; then
      append_unique REQUESTED_MODULES "$module"
    fi
  done
}

select_all_modules() {
  local module
  while IFS= read -r module; do
    append_unique REQUESTED_MODULES "$module"
  done < <(known_modules)
}

collect_module() {
  local module="$1"
  local dependency

  if array_contains "$module" "${REQUIRED_MODULES[@]}"; then
    return
  fi

  if ! is_known_module "$module"; then
    append_unique UNKNOWN_MODULES "$module"
    return
  fi

  if array_contains "$module" "${RESOLVING_MODULES[@]}"; then
    return
  fi
  append_unique RESOLVING_MODULES "$module"

  for dependency in $(dependencies_for "$module"); do
    collect_module "$dependency"
  done

  append_unique REQUIRED_MODULES "$module"
}

build_plan() {
  local module
  local default_path
  local source_key

  for module in "${REQUESTED_MODULES[@]}"; do
    collect_module "$module"
  done

  append_unique SUPPORT_PATHS "module/backend/module-common"
  append_unique SUPPORT_PATHS "module/backend/pom.xml"
  append_unique SUPPORT_PATHS "module/backend/app"
  append_unique SUPPORT_PATHS "module/frontend-web/src/shared"
  append_unique SUPPORT_PATHS "module/frontend-web/src/App.tsx"
  append_unique SUPPORT_PATHS "module/frontend-web/src/main.tsx"
  append_unique SUPPORT_PATHS "module/frontend-web/src/App.css"
  append_unique SUPPORT_PATHS "module/frontend-web/src/index.css"
  append_unique SUPPORT_PATHS "module/frontend-web/public"
  append_unique SUPPORT_PATHS "module/frontend-web/index.html"
  append_unique SUPPORT_PATHS "module/frontend-web/package.json"
  append_unique SUPPORT_PATHS "module/frontend-web/package-lock.json"
  append_unique SUPPORT_PATHS "module/frontend-web/vite.config.ts"
  append_unique SUPPORT_PATHS "module/frontend-web/tsconfig.json"
  append_unique SUPPORT_PATHS "module/frontend-web/tsconfig.app.json"
  append_unique SUPPORT_PATHS "module/frontend-web/tsconfig.node.json"
  append_unique SUPPORT_PATHS "module/env/.env.example"
  append_unique SUPPORT_PATHS "module/env/local/.env.example"

  for module in "${REQUIRED_MODULES[@]}"; do
    if ! array_contains "$module" "${REQUESTED_MODULES[@]}"; then
      append_unique ADDITIONAL_MODULES "$module"
    fi
    source_key="$(source_key_for "$module")"
    append_unique BACKEND_MODULES "module/backend/module-$source_key"
    append_unique FRONTEND_FEATURES "module/frontend-web/src/features/$source_key"
    append_unique FLYWAY_LOCATIONS "$(flyway_location_for "$module")"
    default_path="$(default_path_for "$module")"
    if [[ -n "$default_path" ]]; then
      append_unique DEFAULT_PATHS "$default_path"
    fi
  done

  local path
  for path in "${SUPPORT_PATHS[@]}" "${BACKEND_MODULES[@]}" "${FRONTEND_FEATURES[@]}"; do
    append_unique COPY_PATHS "$path"
  done
}

print_list() {
  echo "Known modules:"
  known_modules | sed 's/^/  - /'
}

print_array() {
  local title="$1"
  shift
  local value
  echo "$title"
  if [[ "$#" -eq 0 ]]; then
    echo "  - (none)"
    return
  fi
  for value in "$@"; do
    echo "  - $value"
  done
}

print_module_details() {
  local module
  echo "Module details:"
  if [[ "${#REQUIRED_MODULES[@]}" -eq 0 ]]; then
    echo "  - (none)"
    return
  fi
  for module in "${REQUIRED_MODULES[@]}"; do
    echo "  - $module: $(display_name_for "$module")"
    echo "    dependencies: $(dependencies_for "$module")"
    echo "    flyway: $(flyway_location_for "$module")"
    echo "    defaultPath: $(default_path_for "$module")"
  done
}

shell_quote() {
  local value="$1"
  value="$(printf '%s' "$value" | sed "s/'/'\\\\''/g")"
  printf "'%s'" "$value"
}

json_quote() {
  local value="$1"
  value="${value//\\/\\\\}"
  value="${value//\"/\\\"}"
  value="${value//$'\n'/\\n}"
  printf '"%s"' "$value"
}

print_json_array_values() {
  local first="true"
  local value

  printf '['
  for value in "$@"; do
    if [[ "$first" == "false" ]]; then
      printf ', '
    fi
    json_quote "$value"
    first="false"
  done
  printf ']'
}

print_json_array_field() {
  local field="$1"
  shift
  printf '  "%s": ' "$field"
  print_json_array_values "$@"
  printf ',\n'
}

print_plan() {
  echo "Module export plan"
  echo "Repository: $REPO_ROOT"
  echo
  print_array "Requested modules:" "${REQUESTED_MODULES[@]}"
  print_array "Required modules:" "${REQUIRED_MODULES[@]}"
  print_array "Additional required modules:" "${ADDITIONAL_MODULES[@]}"
  print_array "Unknown modules:" "${UNKNOWN_MODULES[@]}"
  echo
  print_module_details
  echo
  print_array "Backend module paths:" "${BACKEND_MODULES[@]}"
  print_array "Frontend feature paths:" "${FRONTEND_FEATURES[@]}"
  print_array "Flyway locations:" "${FLYWAY_LOCATIONS[@]}"
  print_array "Default routes:" "${DEFAULT_PATHS[@]}"
  print_array "Integration/support paths to review:" "${SUPPORT_PATHS[@]}"
  echo
  echo "Use --format rsync --target <project-root> to print copy commands."
  echo "Use --execute --target <project-root> to copy files."
}

additional_modules() {
  local module
  for module in "${REQUIRED_MODULES[@]}"; do
    if ! array_contains "$module" "${REQUESTED_MODULES[@]}"; then
      echo "$module"
    fi
  done
}

print_json_modules() {
  local module
  local first_module="true"
  local dependencies
  local source_key

  printf '  "modules": [\n'
  for module in "${REQUIRED_MODULES[@]}"; do
    if [[ "$first_module" == "false" ]]; then
      printf ',\n'
    fi
    dependencies="$(dependencies_for "$module")"
    source_key="$(source_key_for "$module")"
    printf '    {\n'
    printf '      "module": '
    json_quote "$module"
    printf ',\n'
    printf '      "displayName": '
    json_quote "$(display_name_for "$module")"
    printf ',\n'
    printf '      "dependencies": '
    print_json_array_values $dependencies
    printf ',\n'
    printf '      "backendModule": '
    json_quote "module/backend/module-$source_key"
    printf ',\n'
    printf '      "frontendFeature": '
    json_quote "module/frontend-web/src/features/$source_key"
    printf ',\n'
    printf '      "flywayLocation": '
    json_quote "$(flyway_location_for "$module")"
    printf ',\n'
    printf '      "defaultPath": '
    json_quote "$(default_path_for "$module")"
    printf '\n'
    printf '    }'
    first_module="false"
  done
  printf '\n  ]\n'
}

print_json_manifest() {
  printf '{\n'
  printf '  "schemaVersion": "1.1",\n'
  printf '  "generatedAt": '
  json_quote "$(generated_at)"
  printf ',\n'
  printf '  "repository": '
  json_quote "$REPO_ROOT"
  printf ',\n'
  printf '  "source": {\n'
  printf '    "branch": '
  json_quote "$(git_metadata branch)"
  printf ',\n'
  printf '    "commit": '
  json_quote "$(git_metadata commit)"
  printf ',\n'
  printf '    "shortCommit": '
  json_quote "$(git_metadata short_commit)"
  printf ',\n'
  printf '    "tag": '
  json_quote "$(git_metadata tag)"
  printf ',\n'
  printf '    "describe": '
  json_quote "$(git_metadata describe)"
  printf ',\n'
  printf '    "dirty": %s\n' "$(git_is_dirty)"
  printf '  },\n'
  print_json_array_field "requestedModules" "${REQUESTED_MODULES[@]}"
  print_json_array_field "requiredModules" "${REQUIRED_MODULES[@]}"
  print_json_array_field "additionalModules" "${ADDITIONAL_MODULES[@]}"
  print_json_array_field "unknownModules" "${UNKNOWN_MODULES[@]}"
  print_json_array_field "backendModules" "${BACKEND_MODULES[@]}"
  print_json_array_field "frontendFeatures" "${FRONTEND_FEATURES[@]}"
  print_json_array_field "flywayLocations" "${FLYWAY_LOCATIONS[@]}"
  print_json_array_field "defaultPaths" "${DEFAULT_PATHS[@]}"
  print_json_array_field "supportPaths" "${SUPPORT_PATHS[@]}"
  print_json_array_field "copyPaths" "${COPY_PATHS[@]}"
  print_json_modules
  printf '}\n'
}

print_bundle_readme() {
  local module

  echo "# Module Bundle"
  echo
  echo "This portable bundle was generated from the enterprise modular component repository."
  echo
  echo "## Source"
  echo
  echo "- Repository: \`$REPO_ROOT\`"
  echo "- Branch: \`$(git_metadata branch)\`"
  echo "- Commit: \`$(git_metadata commit)\`"
  echo "- Describe: \`$(git_metadata describe)\`"
  echo "- Dirty worktree at export: \`$(git_is_dirty)\`"
  echo "- Generated at: \`$(generated_at)\`"
  echo
  echo "## Requested Modules"
  for module in "${REQUESTED_MODULES[@]}"; do
    echo "- \`$module\`"
  done
  echo
  echo "## Included Modules"
  for module in "${REQUIRED_MODULES[@]}"; do
    echo "- \`$module\` - $(display_name_for "$module")"
  done
  echo
  echo "## Required Verification"
  echo
  echo "\`\`\`bash"
  echo "mvn -f module/backend/pom.xml test"
  echo "cd module/frontend-web && npm ci && npm run build"
  echo "docker compose -f module/docker/local/docker-compose.yml config"
  echo "\`\`\`"
  echo
  echo "Machine-readable details are stored in \`module/module-bundle-manifest.json\`."
}

print_comma_list() {
  local first="true"
  local value

  for value in "$@"; do
    if [[ "$first" == "false" ]]; then
      printf ','
    fi
    printf '%s' "$value"
    first="false"
  done
}

print_config_snippet() {
  local module
  local enabled

  echo "# Module export configuration snippet"
  echo "# Requested modules: $(print_comma_list "${REQUESTED_MODULES[@]}")"
  echo "# Required modules: $(print_comma_list "${REQUIRED_MODULES[@]}")"
  if [[ "${#ADDITIONAL_MODULES[@]}" -gt 0 ]]; then
    echo "# Additional required modules: $(print_comma_list "${ADDITIONAL_MODULES[@]}")"
  fi
  if [[ "${#UNKNOWN_MODULES[@]}" -gt 0 ]]; then
    echo "# Unknown modules ignored: $(print_comma_list "${UNKNOWN_MODULES[@]}")"
  fi
  echo
  echo "spring:"
  echo "  flyway:"
  echo "    locations: >-"
  echo "      $(print_comma_list "${FLYWAY_LOCATIONS[@]}")"
  echo
  echo "modules:"
  while IFS= read -r module; do
    enabled="false"
    if array_contains "$module" "${REQUIRED_MODULES[@]}"; then
      enabled="true"
    fi
    printf '  %s: %s\n' "$module" "$enabled"
  done < <(known_modules)
}

print_markdown_catalog() {
  local module
  local dependencies
  local default_path
  local source_key

  echo "# 模組清冊 / Module Catalog"
  echo
  echo "本文件由 \`module/backend/module-system/src/main/resources/module-catalog.tsv\` 生成，用於確認可移植模組的依賴、路由與來源位置。"
  echo
  echo "| Module | Name | Phase | Priority | Dependencies | Backend | Frontend | Flyway | Default route |"
  echo "|---|---|---|---|---|---|---|---|---|"
  for module in "${REQUIRED_MODULES[@]}"; do
    dependencies="$(dependencies_for "$module")"
    if [[ -z "$dependencies" ]]; then
      dependencies="-"
    else
      dependencies="${dependencies// /, }"
    fi
    default_path="$(default_path_for "$module")"
    if [[ -z "$default_path" ]]; then
      default_path="-"
    fi
    source_key="$(source_key_for "$module")"
    printf '| `%s` | %s | `%s` | `%s` | %s | `module/backend/module-%s` | `module/frontend-web/src/features/%s` | `%s` | `%s` |\n' \
      "$module" \
      "$(display_name_for "$module")" \
      "$(phase_for "$module")" \
      "$(priority_for "$module")" \
      "$dependencies" \
      "$source_key" \
      "$source_key" \
      "$(flyway_location_for "$module")" \
      "$default_path"
  done
}

print_backend_parent_pom() {
  local module
  local source_key

  cat <<'XML'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.4</version>
        <relativePath/>
    </parent>

    <groupId>com.enterprise</groupId>
    <artifactId>enterprise-backend</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>
    <name>Enterprise Backend</name>
    <description>Portable enterprise module bundle</description>

    <properties>
        <java.version>21</java.version>
        <jjwt.version>0.12.5</jjwt.version>
        <lombok.version>1.18.32</lombok.version>
    </properties>

    <modules>
        <module>module-common</module>
XML
  for module in "${REQUIRED_MODULES[@]}"; do
    source_key="$(source_key_for "$module")"
    printf '        <module>module-%s</module>\n' "$source_key"
  done
  cat <<'XML'
        <module>app</module>
    </modules>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
                <configuration>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>${lombok.version}</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </build>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>com.enterprise</groupId>
                <artifactId>module-common</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-api</artifactId>
                <version>${jjwt.version}</version>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-impl</artifactId>
                <version>${jjwt.version}</version>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-jackson</artifactId>
                <version>${jjwt.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
XML
}

print_app_pom() {
  local module
  local source_key

  cat <<'XML'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.enterprise</groupId>
        <artifactId>enterprise-backend</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>app</artifactId>
    <name>app</name>
    <description>Portable application module</description>

    <dependencies>
        <dependency>
            <groupId>com.enterprise</groupId>
            <artifactId>module-common</artifactId>
        </dependency>
XML
  for module in "${REQUIRED_MODULES[@]}"; do
    source_key="$(source_key_for "$module")"
    printf '        <dependency>\n'
    printf '            <groupId>com.enterprise</groupId>\n'
    printf '            <artifactId>module-%s</artifactId>\n' "$source_key"
    printf '            <version>${project.version}</version>\n'
    printf '        </dependency>\n'
  done
  cat <<'XML'
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
XML
}

print_application_java() {
  local module
  local source_key

  cat <<'JAVA'
package com.enterprise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @file Application.java
 * @description 可移植後端應用程式進入點 / Portable backend application entry point
 * @description_en Starts the selected enterprise modules for an imported project
 * @description_zh 啟動已導入專案選用的企業模組
 */
@SpringBootApplication(scanBasePackages = {
        "com.enterprise.common"
JAVA
  for module in "${REQUIRED_MODULES[@]}"; do
    source_key="$(source_key_for "$module")"
    printf '        , "com.enterprise.%s.config"\n' "$source_key"
  done
  cat <<'JAVA'
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
JAVA
}

print_application_yml() {
  local module

  cat <<'YAML'
server:
  port: ${SERVER_PORT:8080}

spring:
  application:
    name: enterprise-backend
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${POSTGRES_PORT:5432}/${DB_NAME:enterprise_db}
    username: ${DB_USER:postgres}
    password: ${DB_PASSWORD:postgres}
    driver-class-name: org.postgresql.Driver
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: >-
YAML
  printf '      %s\n' "$(print_comma_list "${FLYWAY_LOCATIONS[@]}")"
  cat <<'YAML'
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

jwt:
  secret: ${JWT_SECRET:please-change-this-development-secret-before-deploy}
  expiration: ${JWT_EXPIRATION:900000}

modules:
YAML
  while IFS= read -r module; do
    if array_contains "$module" "${REQUIRED_MODULES[@]}"; then
      printf '  %s: true\n' "$module"
    else
      printf '  %s: false\n' "$module"
    fi
  done < <(known_modules)
}

print_route_imports_for_module() {
  case "$1" in
    auth)
      echo "import { RoleListPage } from './features/auth/pages/RoleListPage';"
      ;;
    organization)
      echo "import { CompanyPage } from './features/organization/pages/CompanyPage';"
      echo "import { DepartmentPage } from './features/organization/pages/DepartmentPage';"
      echo "import { PositionPage } from './features/organization/pages/PositionPage';"
      echo "import { EmployeeListPage } from './features/organization/pages/EmployeeListPage';"
      ;;
    workflow)
      echo "import { DefinitionListPage } from './features/workflow/pages/DefinitionListPage';"
      echo "import { MyTasksPage } from './features/workflow/pages/MyTasksPage';"
      ;;
    notification)
      echo "import { NotificationBell } from './features/notification/components/NotificationBell';"
      ;;
    attendance)
      echo "import { ClockInPage } from './features/attendance/pages/ClockInPage';"
      echo "import { AttendanceRecordsPage } from './features/attendance/pages/AttendanceRecordsPage';"
      echo "import { ShiftManagementPage } from './features/attendance/pages/ShiftManagementPage';"
      echo "import { AttendanceReportPage } from './features/attendance/pages/AttendanceReportPage';"
      ;;
    leave)
      echo "import { LeaveRequestPage } from './features/leave/pages/LeaveRequestPage';"
      echo "import { LeaveApprovalPage } from './features/leave/pages/LeaveApprovalPage';"
      ;;
    system) echo "import { SystemSettingsPage } from './features/system/pages/SystemSettingsPage';" ;;
    audit) echo "import { AuditLogPage } from './features/audit/pages/AuditLogPage';" ;;
    finance) echo "import { FinanceDashboardPage } from './features/finance/pages/FinanceDashboardPage';" ;;
    payroll) echo "import { PayrollDashboardPage } from './features/payroll/pages/PayrollDashboardPage';" ;;
    project) echo "import { ProjectDashboardPage } from './features/project/pages/ProjectDashboardPage';" ;;
    document) echo "import { DocumentDashboardPage } from './features/document/pages/DocumentDashboardPage';" ;;
    report) echo "import { ReportDashboardPage } from './features/report/pages/ReportDashboardPage';" ;;
    crm) echo "import { CrmDashboardPage } from './features/crm/pages/CrmDashboardPage';" ;;
    inventory) echo "import { InventoryDashboardPage } from './features/inventory/pages/InventoryDashboardPage';" ;;
    meeting) echo "import { MeetingDashboardPage } from './features/meeting/pages/MeetingDashboardPage';" ;;
    announcement) echo "import { AnnouncementDashboardPage } from './features/announcement/pages/AnnouncementDashboardPage';" ;;
  esac
}

print_route_elements_for_module() {
  case "$1" in
    auth)
      echo '          <Route path="/role" element={<AppLayout><RoleListPage /></AppLayout>} />'
      ;;
    organization)
      echo '          <Route path="/company" element={<AppLayout><CompanyPage /></AppLayout>} />'
      echo '          <Route path="/department" element={<AppLayout><DepartmentPage /></AppLayout>} />'
      echo '          <Route path="/position" element={<AppLayout><PositionPage /></AppLayout>} />'
      echo '          <Route path="/employee" element={<AppLayout><EmployeeListPage /></AppLayout>} />'
      ;;
    workflow)
      echo '          <Route path="/workflow" element={<AppLayout><DefinitionListPage /></AppLayout>} />'
      echo '          <Route path="/my-tasks" element={<AppLayout><MyTasksPage /></AppLayout>} />'
      ;;
    attendance)
      echo '          <Route path="/attendance/clock-in" element={<AppLayout><ClockInPage /></AppLayout>} />'
      echo '          <Route path="/attendance/records" element={<AppLayout><AttendanceRecordsPage /></AppLayout>} />'
      echo '          <Route path="/attendance/shifts" element={<AppLayout><ShiftManagementPage /></AppLayout>} />'
      echo '          <Route path="/attendance/report" element={<AppLayout><AttendanceReportPage /></AppLayout>} />'
      ;;
    leave)
      echo '          <Route path="/leave/requests" element={<AppLayout><LeaveRequestPage /></AppLayout>} />'
      echo '          <Route path="/leave/approval" element={<AppLayout><LeaveApprovalPage /></AppLayout>} />'
      ;;
    system) echo '          <Route path="/system" element={<AppLayout><SystemSettingsPage /></AppLayout>} />' ;;
    audit) echo '          <Route path="/audit/logs" element={<AppLayout><AuditLogPage /></AppLayout>} />' ;;
    finance) echo '          <Route path="/finance" element={<AppLayout><FinanceDashboardPage /></AppLayout>} />' ;;
    payroll) echo '          <Route path="/payroll" element={<AppLayout><PayrollDashboardPage /></AppLayout>} />' ;;
    project) echo '          <Route path="/projects" element={<AppLayout><ProjectDashboardPage /></AppLayout>} />' ;;
    document) echo '          <Route path="/documents" element={<AppLayout><DocumentDashboardPage /></AppLayout>} />' ;;
    report) echo '          <Route path="/reports" element={<AppLayout><ReportDashboardPage /></AppLayout>} />' ;;
    crm) echo '          <Route path="/crm" element={<AppLayout><CrmDashboardPage /></AppLayout>} />' ;;
    inventory) echo '          <Route path="/inventory" element={<AppLayout><InventoryDashboardPage /></AppLayout>} />' ;;
    meeting) echo '          <Route path="/meetings" element={<AppLayout><MeetingDashboardPage /></AppLayout>} />' ;;
    announcement) echo '          <Route path="/announcements" element={<AppLayout><AnnouncementDashboardPage /></AppLayout>} />' ;;
  esac
}

print_navigation_items_for_module() {
  case "$1" in
    organization)
      echo "  { module: 'organization', label: '組織管理', path: '/department' },"
      echo "  { module: 'organization', label: '員工管理', path: '/employee' },"
      ;;
    workflow)
      echo "  { module: 'workflow', label: '發起簽核', path: '/workflow' },"
      echo "  { module: 'workflow', label: '我的待辦', path: '/my-tasks' },"
      ;;
    attendance)
      echo "  { module: 'attendance', label: '打卡', path: '/attendance/clock-in' },"
      echo "  { module: 'attendance', label: '出勤記錄', path: '/attendance/records' },"
      ;;
    leave)
      echo "  { module: 'leave', label: '請假', path: '/leave/requests' },"
      echo "  { module: 'leave', label: '請假審核', path: '/leave/approval' },"
      ;;
    system) echo "  { module: 'system', label: '系統設定', path: '/system' }," ;;
    audit) echo "  { module: 'audit', label: '稽核日誌', path: '/audit/logs' }," ;;
    finance) echo "  { module: 'finance', label: '財務', path: '/finance' }," ;;
    payroll) echo "  { module: 'payroll', label: '薪資', path: '/payroll' }," ;;
    project) echo "  { module: 'project', label: '專案', path: '/projects' }," ;;
    document) echo "  { module: 'document', label: '文件', path: '/documents' }," ;;
    report) echo "  { module: 'report', label: '報表', path: '/reports' }," ;;
    crm) echo "  { module: 'crm', label: '客戶', path: '/crm' }," ;;
    inventory) echo "  { module: 'inventory', label: '庫存', path: '/inventory' }," ;;
    meeting) echo "  { module: 'meeting', label: '會議', path: '/meetings' }," ;;
    announcement) echo "  { module: 'announcement', label: '公告', path: '/announcements' }," ;;
  esac
}

print_frontend_app() {
  local module
  local default_route="${DEFAULT_PATHS[0]:-/login}"

  cat <<'TSX'
/**
 * @file App.tsx
 * @description 可移植前端路由入口 / Portable frontend route entry
 * @description_en Wires only the selected module routes for an imported project
 * @description_zh 僅組裝已導入專案選用模組的前端路由
 */

import type { ReactNode } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, Link } from 'react-router-dom';
import { Box, AppBar, Toolbar, Typography, Button, Container, CssBaseline } from '@mui/material';

import { LoginPage } from './features/auth/pages/LoginPage';
TSX
  for module in "${REQUIRED_MODULES[@]}"; do
    print_route_imports_for_module "$module"
  done
  cat <<'TSX'
import { NAVIGATION_ITEMS } from './shared/navigation/moduleNavigation';

const AppLayout = ({ children }: { children: ReactNode }) => (
  <Box sx={{ flexGrow: 1, minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          模塊化企業系統
        </Typography>
TSX
  if array_contains "notification" "${REQUIRED_MODULES[@]}"; then
    echo "        <NotificationBell />"
  fi
  cat <<'TSX'
        {NAVIGATION_ITEMS.map((item) => (
          <Button key={`${item.module}-${item.path}`} color="inherit" component={Link} to={item.path}>
            {item.label}
          </Button>
        ))}
        <Button color="inherit" component={Link} to="/login">登出</Button>
      </Toolbar>
    </AppBar>
    <Container sx={{ mt: 4, flexGrow: 1 }}>
      {children}
    </Container>
  </Box>
);

function App() {
  return (
    <>
      <CssBaseline />
      <Router>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
TSX
  for module in "${REQUIRED_MODULES[@]}"; do
    print_route_elements_for_module "$module"
  done
  cat <<TSX
          <Route path="/" element={<Navigate to="$default_route" replace />} />
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </Router>
    </>
  );
}

export default App;
TSX
}

print_frontend_navigation() {
  local module
  local first="true"

  cat <<'TS'
/**
 * @file moduleNavigation.ts
 * @description 可移植模組導覽常數 / Portable module navigation constants
 * @description_en Defines navigation metadata for the selected imported modules
 * @description_zh 定義已導入專案選用模組的導覽資料
 */

export type ModuleKey =
TS
  for module in "${REQUIRED_MODULES[@]}"; do
    if [[ "$first" == "true" ]]; then
      printf "  | '%s'\n" "$module"
      first="false"
    else
      printf "  | '%s'\n" "$module"
    fi
  done
  cat <<'TS'
;

export type EnabledModules = Record<ModuleKey, boolean>;

export interface NavigationItem {
  module: ModuleKey;
  label: string;
  path: string;
}

export const DEFAULT_ENABLED_MODULES: EnabledModules = {
TS
  for module in "${REQUIRED_MODULES[@]}"; do
    printf '  %s: true,\n' "$module"
  done
  cat <<'TS'
};

export const NAVIGATION_ITEMS: NavigationItem[] = [
TS
  for module in "${REQUIRED_MODULES[@]}"; do
    print_navigation_items_for_module "$module"
  done
  cat <<'TS'
];
TS
}

write_portable_files() {
  local target_backend="$TARGET_ROOT/module/backend"
  local target_frontend="$TARGET_ROOT/module/frontend-web"

  mkdir -p "$TARGET_ROOT/module"
  print_backend_parent_pom > "$target_backend/pom.xml"
  print_app_pom > "$target_backend/app/pom.xml"
  print_application_java > "$target_backend/app/src/main/java/com/enterprise/Application.java"
  print_application_yml > "$target_backend/app/src/main/resources/application.yml"
  rm -rf "$target_backend/app/src/test/java"

  print_frontend_app > "$target_frontend/src/App.tsx"
  print_frontend_navigation > "$target_frontend/src/shared/navigation/moduleNavigation.ts"
  print_json_manifest > "$TARGET_ROOT/module/module-bundle-manifest.json"
  print_bundle_readme > "$TARGET_ROOT/module/MODULE_BUNDLE.md"
}

copy_paths() {
  printf '%s\n' "${COPY_PATHS[@]}"
}

ensure_path_exists() {
  local path="$1"
  if [[ ! -e "$REPO_ROOT/$path" ]]; then
    echo "Missing source path: $path" >&2
    return 1
  fi
}

print_rsync_commands() {
  local target="${TARGET_ROOT:-TARGET_PROJECT_ROOT}"
  local path
  local parent
  local source_path
  local target_parent

  echo "# Review before running. Integration files may overwrite target project customizations."
  if [[ "${#UNKNOWN_MODULES[@]}" -gt 0 ]]; then
    echo "# Unknown modules were ignored: ${UNKNOWN_MODULES[*]}"
  fi
  while IFS= read -r path; do
    [[ -z "$path" ]] && continue
    ensure_path_exists "$path"
    parent="$(dirname "$path")"
    source_path="$REPO_ROOT/$path"
    target_parent="$target/$parent"
    printf 'mkdir -p %s\n' "$(shell_quote "$target_parent")"
    printf 'rsync -a --exclude target --exclude node_modules --exclude dist %s %s\n' "$(shell_quote "$source_path")" "$(shell_quote "$target_parent/")"
  done < <(copy_paths)
}

execute_copy() {
  local path
  local parent

  if [[ -z "$TARGET_ROOT" ]]; then
    echo "--execute requires --target <path>" >&2
    exit 1
  fi

  mkdir -p "$TARGET_ROOT"
  while IFS= read -r path; do
    [[ -z "$path" ]] && continue
    ensure_path_exists "$path"
    parent="$(dirname "$path")"
    mkdir -p "$TARGET_ROOT/$parent"
    rsync -a --exclude target --exclude node_modules --exclude dist "$REPO_ROOT/$path" "$TARGET_ROOT/$parent/"
  done < <(copy_paths)

  find "$TARGET_ROOT/module/backend" -name target -type d -prune -exec rm -rf {} +
  rm -rf "$TARGET_ROOT/module/frontend-web/node_modules" "$TARGET_ROOT/module/frontend-web/dist"
  write_portable_files
}

while [[ "$#" -gt 0 ]]; do
  case "$1" in
    --modules)
      MODULES_INPUT="${2:-}"
      shift 2
      ;;
    --all)
      MODULES_INPUT="__all__"
      shift
      ;;
    --target)
      TARGET_ROOT="${2:-}"
      shift 2
      ;;
    --format)
      FORMAT="${2:-}"
      shift 2
      ;;
    --execute)
      EXECUTE="true"
      shift
      ;;
    --list)
      ensure_catalog_file
      print_list
      exit 0
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown option: $1" >&2
      usage >&2
      exit 1
      ;;
  esac
done

ensure_catalog_file

if [[ "$MODULES_INPUT" == "__all__" ]]; then
  select_all_modules
elif [[ -n "$MODULES_INPUT" ]]; then
  normalize_modules "$MODULES_INPUT"
else
  echo "Please provide --modules <list> or --all." >&2
  usage >&2
  exit 1
fi

if [[ "${#REQUESTED_MODULES[@]}" -eq 0 ]]; then
  echo "No valid module keys were provided." >&2
  exit 1
fi

case "$FORMAT" in
  plan|json|config|markdown|rsync)
    ;;
  *)
    echo "Unsupported format: $FORMAT" >&2
    exit 1
    ;;
esac

build_plan

if [[ "$EXECUTE" == "true" ]]; then
  execute_copy
  echo "Copied ${#REQUIRED_MODULES[@]} module(s) and support paths to $TARGET_ROOT"
elif [[ "$FORMAT" == "rsync" ]]; then
  print_rsync_commands
elif [[ "$FORMAT" == "json" ]]; then
  print_json_manifest
elif [[ "$FORMAT" == "config" ]]; then
  print_config_snippet
elif [[ "$FORMAT" == "markdown" ]]; then
  print_markdown_catalog
else
  print_plan
fi
