#!/usr/bin/env bash
#
# @file module-verify-import.sh
# @description 模組導入驗收工具 / Module import verification helper
# @description_en Validates an imported portable module bundle in a target project
# @description_zh 驗證目標專案中的 portable bundle manifest、後端、前端與 Docker Compose 設定

set -eo pipefail

TARGET_ROOT=""
ALLOW_DIRTY_SOURCE="false"
SKIP_BACKEND="false"
SKIP_FRONTEND="false"
SKIP_COMPOSE="false"

usage() {
  cat <<'USAGE'
模組導入驗收工具 / Module import verifier

Usage:
  scripts/module-verify-import.sh --target /path/to/target-project
  scripts/module-verify-import.sh --target /path/to/target-project --allow-dirty-source
  scripts/module-verify-import.sh --target /path/to/target-project --skip-compose

Options:
  --target <path>        Target project root containing module/module-bundle-manifest.json.
  --allow-dirty-source   Allow a bundle exported from a dirty source repository.
  --skip-backend         Skip Maven backend tests.
  --skip-frontend        Skip frontend npm audit/build checks.
  --skip-compose         Skip Docker Compose config validation.
  -h, --help             Show this help.
USAGE
}

run_step() {
  local title="$1"
  shift

  echo "==> $title"
  "$@"
}

require_file() {
  local path="$1"
  if [[ ! -f "$path" ]]; then
    echo "Missing required file: $path" >&2
    exit 1
  fi
}

require_dir() {
  local path="$1"
  if [[ ! -d "$path" ]]; then
    echo "Missing required directory: $path" >&2
    exit 1
  fi
}

manifest_value() {
  local manifest_path="$1"
  local expression="$2"

  python3 - "$manifest_path" "$expression" <<'PY'
import json
import sys
from pathlib import Path

manifest = json.loads(Path(sys.argv[1]).read_text(encoding="utf-8"))
value = manifest
for key in sys.argv[2].split("."):
    value = value[key]
print(value)
PY
}

validate_manifest() {
  local manifest_path="$1"

  python3 - "$manifest_path" "$ALLOW_DIRTY_SOURCE" <<'PY'
import json
import sys
from pathlib import Path

manifest = json.loads(Path(sys.argv[1]).read_text(encoding="utf-8"))
allow_dirty = sys.argv[2] == "true"

required_fields = [
    "schemaVersion",
    "generatedAt",
    "repository",
    "source",
    "requestedModules",
    "requiredModules",
    "backendModules",
    "frontendFeatures",
    "flywayLocations",
    "copyPaths",
    "modules",
]
missing = [field for field in required_fields if field not in manifest]
if missing:
    raise SystemExit(f"manifest missing fields: {', '.join(missing)}")

source = manifest["source"]
for field in ["branch", "commit", "shortCommit", "describe", "dirty"]:
    if field not in source:
        raise SystemExit(f"manifest source missing field: {field}")

if source["dirty"] and not allow_dirty:
    raise SystemExit("bundle source.dirty is true; re-export from a clean source or pass --allow-dirty-source")

if not manifest["requestedModules"]:
    raise SystemExit("manifest requestedModules is empty")
if not manifest["requiredModules"]:
    raise SystemExit("manifest requiredModules is empty")

print(f"schema={manifest['schemaVersion']}")
print(f"source={source['shortCommit']} dirty={str(source['dirty']).lower()}")
print("requested=" + ",".join(manifest["requestedModules"]))
print("required=" + ",".join(manifest["requiredModules"]))
PY
}

while [[ "$#" -gt 0 ]]; do
  case "$1" in
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

if [[ -z "$TARGET_ROOT" ]]; then
  echo "Please provide --target <path>." >&2
  usage >&2
  exit 1
fi

MANIFEST_PATH="$TARGET_ROOT/module/module-bundle-manifest.json"
BACKEND_POM="$TARGET_ROOT/module/backend/pom.xml"
FRONTEND_DIR="$TARGET_ROOT/module/frontend-web"
COMPOSE_FILE="$TARGET_ROOT/module/docker/local/docker-compose.yml"

require_file "$MANIFEST_PATH"
run_step "Validate bundle manifest" validate_manifest "$MANIFEST_PATH"

if [[ "$SKIP_BACKEND" != "true" ]]; then
  require_file "$BACKEND_POM"
  run_step "Run backend tests" mvn -f "$BACKEND_POM" test
fi

if [[ "$SKIP_FRONTEND" != "true" ]]; then
  require_dir "$FRONTEND_DIR"
  run_step "Install frontend dependencies" bash -lc "cd '$FRONTEND_DIR' && npm ci"
  run_step "Run frontend audit" bash -lc "cd '$FRONTEND_DIR' && npm audit --audit-level=high"
  run_step "Run frontend build" bash -lc "cd '$FRONTEND_DIR' && npm run build"
fi

if [[ "$SKIP_COMPOSE" != "true" ]]; then
  require_file "$COMPOSE_FILE"
  run_step "Validate Docker Compose config" docker compose -f "$COMPOSE_FILE" config
fi

echo "Module import verification completed successfully."
