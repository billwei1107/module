#!/usr/bin/env bash
#
# @file module-release-check.sh
# @description 模組正式發布前檢查工具 / Module release readiness checker
# @description_en Runs the required checks before exporting a reusable module bundle for another project
# @description_zh 在模組正式導入其他專案前，驗證分支、乾淨來源、catalog、建置與 portable bundle 匯出驗收

set -eo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

MODULES_INPUT="payroll"
TARGET_ROOT=""
ALLOW_DIRTY_SOURCE="false"
SKIP_BACKEND="false"
SKIP_FRONTEND="false"
SKIP_COMPOSE="false"
SKIP_IMPORT="false"

usage() {
  cat <<'USAGE'
模組正式發布前檢查工具 / Module release readiness checker

Usage:
  scripts/module-release-check.sh --modules payroll
  scripts/module-release-check.sh --modules payroll --target /tmp/module-release-check
  scripts/module-release-check.sh --modules crm --skip-backend --skip-frontend --skip-compose

Options:
  --modules <list>       Comma or space separated module keys. Default: payroll.
  --target <path>        Empty target directory used for export smoke. Defaults to a temp directory.
  --allow-dirty-source   Allow running against a dirty source repository. Not for formal releases.
  --skip-backend         Skip source Maven tests and imported backend tests.
  --skip-frontend        Skip source frontend audit/lint/build and imported frontend audit/build.
  --skip-compose         Skip source and imported Docker Compose config checks.
  --skip-import          Skip imported bundle verification after export.
  -h, --help             Show this help.
USAGE
}

run_step() {
  local title="$1"
  shift

  echo "==> $title"
  "$@"
}

require_clean_source() {
  if [[ "$ALLOW_DIRTY_SOURCE" == "true" ]]; then
    echo "WARN: dirty source is allowed for this run; do not use this as a formal release gate." >&2
    return
  fi

  if [[ -n "$(git -C "$REPO_ROOT" status --porcelain --untracked-files=all)" ]]; then
    echo "Source repository is dirty. Commit, stash, or remove local changes before formal release." >&2
    exit 1
  fi
}

validate_branch() {
  local branch
  branch="$(git -C "$REPO_ROOT" rev-parse --abbrev-ref HEAD)"

  case "$branch" in
    main|master)
      echo "Formal module release checks must run from a feature/release branch, not $branch." >&2
      exit 1
      ;;
  esac
}

prepare_target() {
  if [[ -z "$TARGET_ROOT" ]]; then
    TARGET_ROOT="$(mktemp -d "${TMPDIR:-/tmp}/module-release-check.XXXXXX")"
    return
  fi

  mkdir -p "$TARGET_ROOT"
  if find "$TARGET_ROOT" -mindepth 1 -maxdepth 1 | read -r _; then
    echo "Release check target must be empty: $TARGET_ROOT" >&2
    exit 1
  fi
}

validate_catalog_docs() {
  local generated_catalog
  generated_catalog="$(mktemp "${TMPDIR:-/tmp}/module-catalog.XXXXXX")"

  "$REPO_ROOT/scripts/module-export.sh" --all --format markdown > "$generated_catalog"
  diff -u "$REPO_ROOT/docs/module-catalog.md" "$generated_catalog"
}

validate_script_syntax() {
  local script

  for script in \
    "$REPO_ROOT/scripts/module-export.sh" \
    "$REPO_ROOT/scripts/module-manifest-diff.sh" \
    "$REPO_ROOT/scripts/module-release-check.sh" \
    "$REPO_ROOT/scripts/module-verify-import.sh"; do
    bash -n "$script"
  done
}

validate_manifest_source() {
  local manifest_path="$1"
  local allow_dirty="$2"
  local expected_branch
  local expected_commit

  expected_branch="$(git -C "$REPO_ROOT" rev-parse --abbrev-ref HEAD)"
  expected_commit="$(git -C "$REPO_ROOT" rev-parse HEAD)"

  python3 - "$manifest_path" "$allow_dirty" "$expected_branch" "$expected_commit" <<'PY'
import json
import sys
from pathlib import Path

manifest = json.loads(Path(sys.argv[1]).read_text(encoding="utf-8"))
allow_dirty = sys.argv[2] == "true"
expected_branch = sys.argv[3]
expected_commit = sys.argv[4]
source = manifest.get("source", {})

if manifest.get("schemaVersion") != "1.1":
    raise SystemExit("manifest schemaVersion must be 1.1")
if source.get("branch") != expected_branch:
    raise SystemExit(f"manifest source.branch mismatch: {source.get('branch')} != {expected_branch}")
if source.get("commit") != expected_commit:
    raise SystemExit("manifest source.commit does not match current HEAD")
if source.get("dirty") and not allow_dirty:
    raise SystemExit("manifest source.dirty must be false for formal release")
if not manifest.get("requestedModules"):
    raise SystemExit("manifest requestedModules is empty")
if not manifest.get("requiredModules"):
    raise SystemExit("manifest requiredModules is empty")

print(f"source={source.get('shortCommit')} dirty={str(source.get('dirty')).lower()}")
print("requested=" + ",".join(manifest["requestedModules"]))
print("required=" + ",".join(manifest["requiredModules"]))
PY
}

run_import_verifier() {
  local args=("--target" "$TARGET_ROOT")

  if [[ "$ALLOW_DIRTY_SOURCE" == "true" ]]; then
    args+=("--allow-dirty-source")
  fi
  if [[ "$SKIP_BACKEND" == "true" ]]; then
    args+=("--skip-backend")
  fi
  if [[ "$SKIP_FRONTEND" == "true" ]]; then
    args+=("--skip-frontend")
  fi
  if [[ "$SKIP_COMPOSE" == "true" ]]; then
    args+=("--skip-compose")
  fi

  "$REPO_ROOT/scripts/module-verify-import.sh" "${args[@]}"
}

while [[ "$#" -gt 0 ]]; do
  case "$1" in
    --modules)
      MODULES_INPUT="${2:-}"
      shift 2
      ;;
    --target)
      TARGET_ROOT="${2:-}"
      shift 2
      ;;
    --allow-dirty-source)
      ALLOW_DIRTY_SOURCE="true"
      shift
      ;;
    --skip-backend)
      SKIP_BACKEND="true"
      shift
      ;;
    --skip-frontend)
      SKIP_FRONTEND="true"
      shift
      ;;
    --skip-compose)
      SKIP_COMPOSE="true"
      shift
      ;;
    --skip-import)
      SKIP_IMPORT="true"
      shift
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

if [[ -z "$MODULES_INPUT" ]]; then
  echo "Please provide non-empty --modules <list>." >&2
  exit 1
fi

cd "$REPO_ROOT"

run_step "Validate release branch" validate_branch
run_step "Validate clean source" require_clean_source
run_step "Validate script syntax" validate_script_syntax
run_step "Validate generated module catalog" validate_catalog_docs

if [[ "$SKIP_BACKEND" != "true" ]]; then
  run_step "Run source backend tests" mvn -f "$REPO_ROOT/module/backend/pom.xml" test
fi

if [[ "$SKIP_FRONTEND" != "true" ]]; then
  run_step "Install source frontend dependencies" bash -lc "cd '$REPO_ROOT/module/frontend-web' && npm ci"
  run_step "Run source frontend audit" bash -lc "cd '$REPO_ROOT/module/frontend-web' && npm audit --audit-level=high"
  run_step "Run source frontend lint" bash -lc "cd '$REPO_ROOT/module/frontend-web' && npm run lint -- --max-warnings=0"
  run_step "Run source frontend build" bash -lc "cd '$REPO_ROOT/module/frontend-web' && npm run build"
fi

if [[ "$SKIP_COMPOSE" != "true" ]]; then
  run_step "Validate source Docker Compose config" docker compose -f "$REPO_ROOT/module/docker/local/docker-compose.yml" config
fi

run_step "Prepare export target" prepare_target

export_args=("--modules" "$MODULES_INPUT" "--target" "$TARGET_ROOT" "--execute")
if [[ "$ALLOW_DIRTY_SOURCE" != "true" ]]; then
  export_args+=("--require-clean")
fi

run_step "Export portable bundle" "$REPO_ROOT/scripts/module-export.sh" "${export_args[@]}"
run_step "Validate bundle manifest source" validate_manifest_source "$TARGET_ROOT/module/module-bundle-manifest.json" "$ALLOW_DIRTY_SOURCE"

if [[ "$SKIP_IMPORT" != "true" ]]; then
  run_step "Verify imported bundle" run_import_verifier
fi

echo "Module release check passed."
echo "Target: $TARGET_ROOT"
