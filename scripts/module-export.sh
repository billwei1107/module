#!/usr/bin/env bash
#
# @file module-export.sh
# @description 模組搬移計畫與 rsync 匯出工具 / Module export planning and rsync helper
# @description_en Expands module dependencies and prints or executes reusable component copy steps
# @description_zh 依所選模組展開依賴，輸出跨專案搬移需要的後端、前端與整合檔案清單

set -eo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

MODULES_INPUT=""
TARGET_ROOT=""
FORMAT="plan"
EXECUTE="false"

REQUESTED_MODULES=()
REQUIRED_MODULES=()
UNKNOWN_MODULES=()
RESOLVING_MODULES=()
BACKEND_MODULES=()
FRONTEND_FEATURES=()
FLYWAY_LOCATIONS=()
DEFAULT_PATHS=()
SUPPORT_PATHS=()

usage() {
  cat <<'USAGE'
模組搬移工具 / Module export helper

Usage:
  scripts/module-export.sh --modules payroll,leave
  scripts/module-export.sh --modules payroll --format rsync --target /path/to/project
  scripts/module-export.sh --modules payroll --target /path/to/project --execute
  scripts/module-export.sh --all
  scripts/module-export.sh --list

Options:
  --modules <list>   Comma or space separated module keys.
  --all              Select all reusable modules.
  --target <path>    Target project root for rsync commands or execution.
  --format <format>  plan or rsync. Default: plan.
  --execute          Execute rsync copy. Requires --target.
  --list             Print known module keys.
  -h, --help         Show this help.

Default mode is dry-run. It prints a reviewable plan and never writes to a target
project unless --execute is provided.
USAGE
}

known_modules() {
  cat <<'MODULES'
auth
organization
workflow
notification
attendance
leave
system
audit
finance
payroll
project
document
report
crm
inventory
meeting
announcement
MODULES
}

is_known_module() {
  case "$1" in
    auth|organization|workflow|notification|attendance|leave|system|audit|finance|payroll|project|document|report|crm|inventory|meeting|announcement)
      return 0
      ;;
    *)
      return 1
      ;;
  esac
}

dependencies_for() {
  case "$1" in
    auth)
      echo ""
      ;;
    organization|system|audit)
      echo "auth"
      ;;
    workflow)
      echo "auth organization"
      ;;
    notification|attendance|document|report|crm|inventory)
      echo "auth organization"
      ;;
    leave)
      echo "auth organization workflow attendance"
      ;;
    finance)
      echo "auth organization workflow"
      ;;
    payroll)
      echo "auth organization attendance leave finance"
      ;;
    project|meeting|announcement)
      echo "auth organization notification"
      ;;
    *)
      echo ""
      ;;
  esac
}

display_name_for() {
  case "$1" in
    auth) echo "認證授權 / Authentication" ;;
    organization) echo "組織管理 / Organization" ;;
    workflow) echo "審批流程 / Workflow" ;;
    notification) echo "通知中心 / Notification" ;;
    attendance) echo "打卡考勤 / Attendance" ;;
    leave) echo "請假管理 / Leave Management" ;;
    system) echo "系統設定 / System Settings" ;;
    audit) echo "稽核日誌 / Audit Log" ;;
    finance) echo "財務管理 / Finance" ;;
    payroll) echo "薪資管理 / Payroll" ;;
    project) echo "專案任務 / Project Management" ;;
    document) echo "文件管理 / Document Management" ;;
    report) echo "報表分析 / Report Analytics" ;;
    crm) echo "客戶管理 / CRM" ;;
    inventory) echo "庫存管理 / Inventory" ;;
    meeting) echo "會議管理 / Meeting Management" ;;
    announcement) echo "公告系統 / Announcement" ;;
    *) echo "$1" ;;
  esac
}

default_path_for() {
  case "$1" in
    auth) echo "/login" ;;
    organization) echo "/department" ;;
    workflow) echo "/workflow" ;;
    notification) echo "" ;;
    attendance) echo "/attendance/clock-in" ;;
    leave) echo "/leave/requests" ;;
    system) echo "/system" ;;
    audit) echo "/audit/logs" ;;
    finance) echo "/finance" ;;
    payroll) echo "/payroll" ;;
    project) echo "/projects" ;;
    document) echo "/documents" ;;
    report) echo "/reports" ;;
    crm) echo "/crm" ;;
    inventory) echo "/inventory" ;;
    meeting) echo "/meetings" ;;
    announcement) echo "/announcements" ;;
    *) echo "" ;;
  esac
}

flyway_location_for() {
  if [[ "$1" == "auth" ]]; then
    echo "classpath:db/migration"
    return
  fi
  echo "classpath:db/migration/$1"
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
    append_unique BACKEND_MODULES "module/backend/module-$module"
    append_unique FRONTEND_FEATURES "module/frontend-web/src/features/$module"
    append_unique FLYWAY_LOCATIONS "$(flyway_location_for "$module")"
    default_path="$(default_path_for "$module")"
    if [[ -n "$default_path" ]]; then
      append_unique DEFAULT_PATHS "$default_path"
    fi
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

print_plan() {
  echo "Module export plan"
  echo "Repository: $REPO_ROOT"
  echo
  print_array "Requested modules:" "${REQUESTED_MODULES[@]}"
  print_array "Required modules:" "${REQUIRED_MODULES[@]}"
  print_array "Additional required modules:" $(additional_modules)
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

copy_paths() {
  printf '%s\n' "${SUPPORT_PATHS[@]}"
  printf '%s\n' "${BACKEND_MODULES[@]}"
  printf '%s\n' "${FRONTEND_FEATURES[@]}"
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
  plan|rsync)
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
else
  print_plan
fi
