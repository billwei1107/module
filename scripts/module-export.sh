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

usage() {
  cat <<'USAGE'
模組搬移工具 / Module export helper

Usage:
  scripts/module-export.sh --modules payroll,leave
  scripts/module-export.sh --modules payroll --format json
  scripts/module-export.sh --modules payroll --format config
  scripts/module-export.sh --modules payroll --format rsync --target /path/to/project
  scripts/module-export.sh --modules payroll --target /path/to/project --execute
  scripts/module-export.sh --all
  scripts/module-export.sh --list

Options:
  --modules <list>   Comma or space separated module keys.
  --all              Select all reusable modules.
  --target <path>    Target project root for rsync commands or execution.
  --format <format>  plan, json, config, or rsync. Default: plan.
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
  printf '  "schemaVersion": "1.0",\n'
  printf '  "repository": '
  json_quote "$REPO_ROOT"
  printf ',\n'
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
    printf 'rsync -a %s %s\n' "$(shell_quote "$source_path")" "$(shell_quote "$target_parent/")"
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
    rsync -a "$REPO_ROOT/$path" "$TARGET_ROOT/$parent/"
  done < <(copy_paths)
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
  plan|json|config|rsync)
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
else
  print_plan
fi
