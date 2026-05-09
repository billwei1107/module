#!/usr/bin/env bash
#
# @file module-manifest-diff.sh
# @description 模組 bundle manifest 差異比對工具 / Module bundle manifest diff helper
# @description_en Compares two portable module manifests for upgrade review
# @description_zh 比對兩份 portable bundle manifest，協助既有專案升級前審核模組、Flyway 與來源版本變化

set -eo pipefail

FROM_MANIFEST=""
TO_MANIFEST=""
FORMAT="text"

usage() {
  cat <<'USAGE'
模組 manifest 差異比對工具 / Module manifest diff helper

Usage:
  scripts/module-manifest-diff.sh --from /path/before.json --to /path/after.json
  scripts/module-manifest-diff.sh --from /path/before.json --to /path/after.json --format json

Options:
  --from <path>       Existing target project manifest.
  --to <path>         Newly exported manifest.
  --format <format>   text or json. Default: text.
  -h, --help          Show this help.
USAGE
}

while [[ "$#" -gt 0 ]]; do
  case "$1" in
    --from)
      FROM_MANIFEST="${2:-}"
      shift 2
      ;;
    --to)
      TO_MANIFEST="${2:-}"
      shift 2
      ;;
    --format)
      FORMAT="${2:-}"
      shift 2
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

if [[ -z "$FROM_MANIFEST" || -z "$TO_MANIFEST" ]]; then
  echo "Please provide --from <path> and --to <path>." >&2
  usage >&2
  exit 1
fi

if [[ ! -f "$FROM_MANIFEST" ]]; then
  echo "Missing --from manifest: $FROM_MANIFEST" >&2
  exit 1
fi

if [[ ! -f "$TO_MANIFEST" ]]; then
  echo "Missing --to manifest: $TO_MANIFEST" >&2
  exit 1
fi

case "$FORMAT" in
  text|json)
    ;;
  *)
    echo "Unsupported format: $FORMAT" >&2
    exit 1
    ;;
esac

python3 - "$FROM_MANIFEST" "$TO_MANIFEST" "$FORMAT" <<'PY'
import json
import sys
from pathlib import Path

before = json.loads(Path(sys.argv[1]).read_text(encoding="utf-8"))
after = json.loads(Path(sys.argv[2]).read_text(encoding="utf-8"))
output_format = sys.argv[3]


def as_list(manifest, key):
    value = manifest.get(key, [])
    if not isinstance(value, list):
        raise SystemExit(f"manifest field {key} must be a list")
    return value


def source_summary(manifest):
    source = manifest.get("source", {})
    return {
        "branch": source.get("branch", ""),
        "shortCommit": source.get("shortCommit", ""),
        "tag": source.get("tag", ""),
        "describe": source.get("describe", ""),
        "dirty": bool(source.get("dirty", False)),
    }


def diff_list(key):
    before_values = as_list(before, key)
    after_values = as_list(after, key)
    before_set = set(before_values)
    after_set = set(after_values)
    return {
        "before": before_values,
        "after": after_values,
        "added": [value for value in after_values if value not in before_set],
        "removed": [value for value in before_values if value not in after_set],
    }


fields = [
    "requestedModules",
    "requiredModules",
    "additionalModules",
    "backendModules",
    "frontendFeatures",
    "flywayLocations",
    "defaultPaths",
    "supportPaths",
    "copyPaths",
]

result = {
    "schemaVersion": "1.0",
    "fromSource": source_summary(before),
    "toSource": source_summary(after),
    "changes": {field: diff_list(field) for field in fields},
}

has_changes = any(
    change["added"] or change["removed"]
    for change in result["changes"].values()
)
result["hasChanges"] = has_changes

if output_format == "json":
    print(json.dumps(result, ensure_ascii=False, indent=2))
    raise SystemExit(0)

print("Module manifest diff")
print(f"From: {result['fromSource']['shortCommit'] or '-'} ({result['fromSource']['describe'] or '-'})")
print(f"To:   {result['toSource']['shortCommit'] or '-'} ({result['toSource']['describe'] or '-'})")
print()

if not has_changes:
    print("No module manifest changes.")
    raise SystemExit(0)

for field in fields:
    change = result["changes"][field]
    if not change["added"] and not change["removed"]:
        continue
    print(f"{field}:")
    if change["added"]:
        print("  added:")
        for value in change["added"]:
            print(f"    - {value}")
    if change["removed"]:
        print("  removed:")
        for value in change["removed"]:
            print(f"    - {value}")
    print()
PY
