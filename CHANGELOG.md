# Changelog

All notable changes to the enterprise modular component repository are recorded here.

## Unreleased

### Added
- Added portable module bundle generation for selected modules and recursive dependencies.
- Added generated `module/module-bundle-manifest.json` with export time, source branch, commit, tag, dirty state, selected modules, dependency-expanded modules, copy paths, Flyway locations, and frontend/backend paths.
- Added generated `module/MODULE_BUNDLE.md` with human-readable source metadata, included modules, and required verification commands.
- Added Markdown module catalog generation from `module-catalog.tsv`.
- Added CI checks for portable imports of `crm` and `payroll` bundles.

### Changed
- `scripts/module-export.sh --execute` now rewrites target-side backend POM files, backend app entrypoint, backend `application.yml`, frontend route entry, and frontend navigation to match only the selected module bundle.
- `scripts/module-export.sh --format json` now emits schema version `1.1` with `source.*` metadata for release traceability.

### Fixed
- Prevented partial module exports from copying stale build outputs such as Maven `target/`, frontend `node_modules/`, and frontend `dist/`.
- Fixed portable frontend exports by including `module/frontend-web/index.html` and `module/frontend-web/public`.
- Updated frontend lockfile dependencies to resolve current `npm audit --audit-level=high` findings for `axios`, `follow-redirects`, and `postcss`.
