# Changelog

All notable changes to the enterprise modular component repository are recorded here.

## Unreleased

### Changed
- Clarified that the module source repository now lives outside the POS project root and should be treated as the single writable parent repository.

### Added
- Added `ai-handoff.md` as the shortest handoff entry for AI agents working in target projects.
- Added `ai-module-planning.md` as the AI-first planning entry for selecting reusable modules in new projects.
- Added `ai-module-change-protocol.md` to define how target-project fixes, reusable changes, and new modules are written back to the module source repository.
- Added portable module bundle generation for selected modules and recursive dependencies.
- Added generated `module/module-bundle-manifest.json` with export time, source branch, commit, tag, dirty state, selected modules, dependency-expanded modules, copy paths, Flyway locations, and frontend/backend paths.
- Added generated `module/MODULE_BUNDLE.md` with human-readable source metadata, included modules, and required verification commands.
- Added `scripts/module-verify-import.sh` to validate imported bundles with manifest checks, backend tests, frontend audit/build, and Docker Compose config validation.
- Added `scripts/module-manifest-diff.sh` to compare existing and newly exported bundle manifests before upgrades.
- Added `scripts/module-release-check.sh` to run the formal release readiness gate before cross-project imports.
- Added `scripts/module-export.sh --require-clean` to refuse formal exports when the source repository has local changes.
- Added Markdown module catalog generation from `module-catalog.tsv`.
- Added CI checks for portable imports of `crm` and `payroll` bundles.

### Changed
- `scripts/module-export.sh --execute` now rewrites target-side backend POM files, backend app entrypoint, backend `application.yml`, frontend route entry, and frontend navigation to match only the selected module bundle.
- `scripts/module-export.sh --format json` now emits schema version `1.1` with `source.*` metadata for release traceability.
- Portable exports now include `module/docker/local` so imported targets can validate local Compose and Dockerfile configuration.

### Fixed
- Prevented partial module exports from copying stale build outputs such as Maven `target/`, frontend `node_modules/`, and frontend `dist/`.
- Fixed portable frontend exports by including `module/frontend-web/index.html` and `module/frontend-web/public`.
- Updated frontend lockfile dependencies to resolve current `npm audit --audit-level=high` findings for `axios`, `follow-redirects`, and `postcss`.
- Fixed source dirty detection to include untracked files, preventing formal manifests from hiding unreproducible local files.
- Removed nonportable `module-report` hard dependencies on attendance, finance, and payroll modules so report-only portable bundles can compile cleanly.
