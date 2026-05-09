# 模組發布與導入流程 / Module Release Guide

本文件定義母體倉庫發布可導入基線，以及其他專案導入或升級模組時必須保留的版本追蹤資訊。

## 1. 發布基線

正式導入其他專案前，母體倉庫必須位於乾淨的開發分支或已審核的 release commit：

```bash
git status --short --branch
mvn -f module/backend/pom.xml test
cd module/frontend-web && npm ci && npm audit --audit-level=high && npm run lint -- --max-warnings=0 && npm run build
docker compose -f module/docker/local/docker-compose.yml config
```

建議 tag 格式：

```bash
git tag module-vYYYY.MM.DD.N
git push origin module-vYYYY.MM.DD.N
```

若尚未建立 tag，也可以使用 `module/module-bundle-manifest.json` 的 `source.shortCommit` 作為導入基準，但正式交付仍建議使用 tag。

## 2. 匯出 Bundle

```bash
scripts/module-export.sh --modules payroll --target /path/to/target-project --execute --require-clean
```

匯出後目標專案會取得：

- `module/module-bundle-manifest.json`：機器可讀的來源版本、模組與搬移清單
- `module/MODULE_BUNDLE.md`：人類可讀的來源版本、包含模組與驗證指令
- `module/backend/*`：已裁切的 Maven portable backend bundle
- `module/frontend-web/*`：已裁切的 frontend feature 契約與入口
- `module/env/*`：導入後需要調整的環境變數範本
- `module/docker/local/*`：本地 Docker Compose、Dockerfile 與 Nginx 設定

正式導入前，請確認：

```bash
python3 -m json.tool module/module-bundle-manifest.json >/tmp/module-bundle-manifest.pretty.json
grep '"dirty": false' module/module-bundle-manifest.json
```

`dirty` 必須為 `false`。若為 `true`，代表母體匯出時含未提交變更，不能作為正式導入基準。

## 3. 目標專案導入

在目標專案中提交前至少執行：

```bash
scripts/module-verify-import.sh --target /path/to/target-project
```

若目標專案不方便直接使用母體腳本，則手動執行等價命令：

```bash
mvn -f module/backend/pom.xml test
cd module/frontend-web && npm ci && npm audit --audit-level=high && npm run build
docker compose -f module/docker/local/docker-compose.yml config
```

導入 commit 或 devlog 需記錄：

- 母體來源：`module/module-bundle-manifest.json`
- `source.tag` 或 `source.shortCommit`
- `requestedModules`
- `requiredModules`
- 驗證命令結果

## 4. 升級既有專案

升級前先保存目標專案目前的 manifest：

```bash
cp module/module-bundle-manifest.json /tmp/module-bundle-manifest.before.json
```

再從新母體基線匯出到暫存目錄並比對：

```bash
scripts/module-export.sh --modules payroll --target /tmp/module-upgrade-check --execute --require-clean
diff -u /tmp/module-bundle-manifest.before.json /tmp/module-upgrade-check/module/module-bundle-manifest.json
```

確認模組依賴、Flyway locations、前端 feature 路徑與支援檔符合預期後，再匯入正式目標專案並跑第 3 節驗證。

## 5. 回寫母體

若目標專案修復的是通用 bug 或可複用能力，必須回寫母體倉庫並重新發布基線。專案專屬 UI 可留在目標專案，但 API contract、DTO、模組 key、Flyway schema 與 feature toggle 行為若具通用性，應優先回寫母體。
