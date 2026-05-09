# 模組發布與導入流程 / Module Release Guide

本文件定義母體倉庫發布可導入基線，以及其他專案導入或升級模組時必須保留的版本追蹤資訊。

新專案規劃時先讀根目錄 `ai-module-planning.md`；既有專案追加模組、修正通用 bug 或新增可重用模組時先讀 `ai-module-change-protocol.md`。

母體倉庫是獨立於 POS 或任何目標專案的 Git 倉庫，目前建議本機位置為 `/Users/wei/Desktop/code/模塊化組件/`。其他專案中的 `reference/模塊化組件/` 不應作為發布來源。

## 1. 發布基線

正式導入其他專案前，母體倉庫必須位於乾淨的開發分支或已審核的 release commit：

```bash
scripts/module-release-check.sh --modules payroll
```

這個檢查會驗證目前不在 `main/master`、工作樹乾淨、catalog 文件與 TSV 同步、後端測試、前端 audit/lint/build、Docker Compose config、portable bundle 匯出與導入後驗收。

建議 tag 格式：

```bash
git tag module-vYYYY.MM.DD.N
git push origin module-vYYYY.MM.DD.N
```

若尚未建立 tag，也可以使用 `module/module-bundle-manifest.json` 的 `source.shortCommit` 作為導入基準，但正式交付仍建議使用 tag。

若只想在 CI 或開發中驗證 release gate 腳本與匯出流程，可使用 skip 選項；正式交付不得使用 `--allow-dirty-source`：

```bash
scripts/module-release-check.sh --modules crm --skip-backend --skip-frontend --skip-compose
```

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
scripts/module-manifest-diff.sh \
  --from /tmp/module-bundle-manifest.before.json \
  --to /tmp/module-upgrade-check/module/module-bundle-manifest.json
```

確認模組依賴、Flyway locations、前端 feature 路徑與支援檔符合預期後，再匯入正式目標專案並跑第 3 節驗證。

## 5. 回寫母體

若目標專案修復的是通用 bug 或可複用能力，必須回寫母體倉庫並重新發布基線。專案專屬 UI 可留在目標專案，但 API contract、DTO、模組 key、Flyway schema 與 feature toggle 行為若具通用性，應優先回寫母體。

詳細判斷與操作流程請參考根目錄 `ai-module-change-protocol.md`。
