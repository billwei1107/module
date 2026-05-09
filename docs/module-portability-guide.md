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
```

JSON manifest 可給其他工具或 CI 讀取，主要欄位：

- `schemaVersion`
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

## 4. 產生 Spring Boot 設定片段

```bash
scripts/module-export.sh --modules payroll --format config > /tmp/module-export-config.yml
```

將輸出的以下區塊合併到目標專案的 `application.yml` 或對應 profile：

- `spring.flyway.locations`
- `modules.*`

注意：輸出會列出完整 17 個模組開關。所選模組與遞迴依賴會是 `true`，其餘模組會是 `false`，避免 Spring profile 合併時沿用 base 預設造成業態組合失真。

## 5. 產生 rsync 指令

```bash
scripts/module-export.sh --modules payroll --format rsync --target /path/to/target-project
```

這個模式只印出指令，不會執行。請先檢查目標專案是否已有客製化檔案，特別是：

- `module/backend/pom.xml`
- `module/backend/app`
- `module/frontend-web/src/App.tsx`
- `module/frontend-web/src/shared`
- `module/frontend-web/package.json`

## 6. 執行複製

```bash
scripts/module-export.sh --modules payroll --target /path/to/target-project --execute
```

`--execute` 會使用 `rsync -a` 複製：

- 後端模組：`module/backend/module-*`
- 前端 feature：`module/frontend-web/src/features/*`
- 共用後端：`module/backend/module-common`
- app 整合檔：`module/backend/app`
- 前端共用支援檔：`module/frontend-web/src/shared`
- 前端入口與建置設定
- 環境變數範本

## 7. 導入後檢查

在目標專案中至少執行：

```bash
mvn test
npm run lint -- --max-warnings=0
npm run build
docker compose -f module/docker/local/docker-compose.yml config
```

若目標專案沒有相同 frontend/backend 目錄結構，先使用 `--format json` 或 `--format rsync` 審核，再手動調整搬移路徑。

## 8. 維護注意事項

- 修復通用模組 bug 後，必須回寫母體倉庫並 commit/push。
- 新增通用功能時，先評估是否應抽回母體，而不是只留在客製專案。
- `scripts/module-export.sh` 的依賴關係需與 `module-system` 的 Feature Toggle 清冊保持一致。
- 搬移後若修改目標專案 UI，仍應保留 API contract、型別與模組 key 的相容性。
