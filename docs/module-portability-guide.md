# 模組移植指南 / Module Portability Guide

本文件說明如何從母體倉庫匯出可重用模組，並導入到另一個專案。前端畫面在目標專案通常會被重做；這裡重點是搬移後端業務模組、前端 feature 契約、共用支援檔、Flyway migration 與 Feature Toggle 設定。

## 1. 確認模組 key

```bash
scripts/module-export.sh --list
```

常用範例：

- `payroll`：會自動帶出 `auth`, `organization`, `attendance`, `workflow`, `leave`, `finance`
- `leave`：會自動帶出 `auth`, `organization`, `workflow`, `attendance`
- `crm`：會自動帶出 `auth`, `organization`

## 2. 先看搬移計畫

```bash
scripts/module-export.sh --modules payroll
```

這個模式只輸出計畫，不會寫入任何目標專案。必須先檢查：

- `Required modules`
- `Additional required modules`
- `Backend module paths`
- `Frontend feature paths`
- `Flyway locations`
- `Integration/support paths to review`

## 3. 產生機器可讀 manifest

```bash
scripts/module-export.sh --modules payroll --format json > /tmp/module-export.json
scripts/module-export.sh --modules payroll --format json --require-clean > /tmp/module-export-clean.json
```

JSON manifest 可給其他工具或 CI 讀取，主要欄位：

- `schemaVersion`
- `generatedAt`
- `source.branch`
- `source.commit`
- `source.shortCommit`
- `source.tag`
- `source.describe`
- `source.dirty`
- `requestedModules`
- `requiredModules`
- `additionalModules`
- `unknownModules`
- `backendModules`
- `frontendFeatures`
- `flywayLocations`
- `defaultPaths`
- `supportPaths`
- `copyPaths`
- `modules`

`source.*` 欄位用於追溯匯出的母體版本。若 `source.dirty` 為 `true`，代表匯出時母體工作樹含尚未提交的變更，不建議直接作為正式導入基準。

正式導入建議加上 `--require-clean`，若母體存在未提交或未追蹤檔案，工具會拒絕匯出，避免 manifest 指向不可重現的狀態。

## 4. 產生 Markdown 清冊

```bash
scripts/module-export.sh --all --format markdown > docs/module-catalog.md
```

`docs/module-catalog.md` 由 `module-catalog.tsv` 生成，列出每個模組的依賴、階段、優先級、前後端位置、Flyway location 與預設路由。CI 會檢查文件是否與 TSV 同步。

## 5. 產生 Spring Boot 設定片段

```bash
scripts/module-export.sh --modules payroll --format config > /tmp/module-export-config.yml
```

將輸出的以下區塊合併到目標專案的 `application.yml` 或對應 profile：

- `spring.flyway.locations`
- `modules.*`

注意：輸出會列出完整 17 個模組開關。所選模組與遞迴依賴會是 `true`，其餘模組會是 `false`，避免 Spring profile 合併時沿用 base 預設造成業態組合失真。

## 6. 產生 rsync 指令

```bash
scripts/module-export.sh --modules payroll --format rsync --target /path/to/target-project
```

這個模式只印出指令，不會執行。請先檢查目標專案是否已有客製化檔案，特別是：

- `module/backend/pom.xml`
- `module/backend/app`
- `module/frontend-web/src/App.tsx`
- `module/frontend-web/src/shared`
- `module/frontend-web/package.json`

## 7. 執行複製

```bash
scripts/module-export.sh --modules payroll --target /path/to/target-project --execute
```

`--execute` 會使用 `rsync -a --exclude target --exclude node_modules --exclude dist` 複製：

- 後端模組：`module/backend/module-*`
- 前端 feature：`module/frontend-web/src/features/*`
- 共用後端：`module/backend/module-common`
- app 整合檔：`module/backend/app`
- 前端共用支援檔：`module/frontend-web/src/shared`
- 前端入口與建置設定
- 環境變數範本
- 本地 Docker Compose 與 Dockerfile 設定

執行後工具會自動重寫目標端的 portable 整合檔：

- `module/backend/pom.xml`：只保留 `module-common`、所選模組的遞迴依賴，以及 `app`
- `module/backend/app/pom.xml`：只依賴已匯出的後端模組
- `module/backend/app/src/main/java/com/enterprise/Application.java`：只掃描已匯出模組的 `config` package
- `module/backend/app/src/main/resources/application.yml`：只啟用已匯出模組與對應 Flyway locations
- `module/frontend-web/src/App.tsx`：只匯入已匯出 feature 的頁面
- `module/frontend-web/src/shared/navigation/moduleNavigation.ts`：只保留已匯出模組的導覽資料
- `module/module-bundle-manifest.json`：記錄匯出時間、母體 branch/commit/tag/dirty 狀態與模組清單
- `module/MODULE_BUNDLE.md`：提供人類可讀的 bundle 來源、包含模組與導入後驗證指令

## 8. 導入後檢查

在目標專案中至少執行：

```bash
mvn -f module/backend/pom.xml test
cd module/frontend-web && npm ci && npm audit --audit-level=high && npm run build
docker compose -f module/docker/local/docker-compose.yml config
```

若仍可從母體倉庫執行工具，也可以使用導入驗收腳本：

```bash
scripts/module-verify-import.sh --target /path/to/target-project
```

若目標專案沒有相同 frontend/backend 目錄結構，先使用 `--format json` 或 `--format rsync` 審核，再手動調整搬移路徑。

## 9. 維護注意事項

- 修復通用模組 bug 後，必須回寫母體倉庫並 commit/push。
- 新增通用功能時，先評估是否應抽回母體，而不是只留在客製專案。
- `module/backend/module-system/src/main/resources/module-catalog.tsv` 是模組清冊單一來源，`module-system` Feature Toggle 與 `scripts/module-export.sh` 都會讀取它。
- 搬移後若修改目標專案 UI，仍應保留 API contract、型別與模組 key 的相容性。
- 正式交付或跨專案導入前，請確認 `module/module-bundle-manifest.json` 的 `source.dirty` 為 `false`，並在目標專案提交訊息或 devlog 中記錄 `source.shortCommit`。
- 發布 tag、正式導入與升級流程請參考 `docs/module-release-guide.md`。
