# AI 模組變更與回寫規範 / AI Module Change Protocol

本文件定義在其他專案開發時，如果需要修正既有模組、追加模組或新增通用模組，AI 應如何判斷、同步與回寫本母體倉庫。

## 1. 核心原則

- 目標專案可以客製 UI 與專案專屬流程，但通用 API contract、DTO、module key、Flyway migration 與 feature toggle 行為應回寫母體。
- `reference/模塊化組件/` 若存在於目標專案中，只能作為參考，不是目標專案正式源碼。
- 通用 bug 不可只修在目標專案；必須回到母體倉庫修正、測試、commit、push、tag。
- 新的可重用模組應在母體建立正式模組，再由目標專案透過 export 導入。
- 每次母體變更完成後，都必須重新跑 release readiness gate。

## 2. 母體倉庫位置

本母體已從 POS 專案根目錄獨立出來，正式可修改來源是單獨的 Git 倉庫：

```text
/Users/wei/Desktop/code/模塊化組件/
```

遠端來源：

```text
billwei1107/module
```

不同開發機可 clone 到其他位置，但 AI 執行回寫時必須先定位「真正的母體 Git 倉庫」，不可把目標專案內的 `reference/模塊化組件/` 當成正式修改來源。`reference/模塊化組件/` 只用於讀文件、規劃與執行匯出工具。

## 3. 變更分類

### A. 專案專屬變更

符合以下條件可留在目標專案：

- 只符合單一客戶或單一業態
- 前端版面、品牌、文案或互動設計
- 目標專案自己的報表欄位、流程排列或權限命名
- 不影響母體 API contract、DTO、資料表、module key

處理方式：留在目標專案，記錄為專案客製，不回寫母體。

### B. 通用 bug 修正

符合以下條件必須回寫母體：

- 計算邏輯錯誤，例如薪資、出勤、財務金額
- API contract 或 DTO 缺漏
- Flyway migration、索引、欄位定義問題
- Feature Toggle 或模組依賴判斷錯誤
- 安全、權限、稽核、資料一致性問題

處理方式：先在目標專案確認問題，再回母體修正並發布新基線。

### C. 可重用新能力或新模組

符合以下條件應抽回母體：

- 未來其他 ERP/POS/CRM 專案也會用到
- 可以獨立成 module key
- 有清楚的後端 entity/service/controller/Flyway 邊界
- 前端可形成 feature contract

處理方式：在母體新增正式模組或擴充既有模組，再由目標專案導入。

## 4. 目標專案追加既有模組流程

追加前先讀目標專案現況：

```bash
python3 -m json.tool /path/to/target-project/module/module-bundle-manifest.json
```

先匯出到暫存目錄：

```bash
scripts/module-export.sh \
  --modules payroll \
  --target /tmp/module-upgrade-check \
  --execute \
  --require-clean
```

比對既有 manifest 與新 bundle：

```bash
scripts/module-manifest-diff.sh \
  --from /path/to/target-project/module/module-bundle-manifest.json \
  --to /tmp/module-upgrade-check/module/module-bundle-manifest.json
```

確認差異後再正式導入：

```bash
scripts/module-export.sh \
  --modules payroll \
  --target /path/to/target-project \
  --execute \
  --require-clean
```

導入後驗證：

```bash
scripts/module-verify-import.sh --target /path/to/target-project
```

## 5. 通用 bug 回寫母體流程

1. 在目標專案重現問題，記錄錯誤情境與期望行為。
2. 判斷是否屬於通用模組問題。
3. 回到真正的母體 Git 倉庫，例如 `/Users/wei/Desktop/code/模塊化組件`。
4. 確認位於 feature 或 release 分支，並拉取最新遠端。
5. 在母體修正對應 backend/frontend/Flyway/文件。
6. 補測試或更新既有測試。
7. 執行必要驗證與 release readiness gate。
8. commit/push 母體。
9. 建立新 tag，例如 `module-v2026.05.09.2`。
10. 回目標專案使用新 tag 匯出或升級，並記錄來源。

母體驗證命令：

```bash
scripts/module-release-check.sh --modules payroll
```

若只修特定模組，可先跑局部測試，但正式發布前仍需跑 release check。

## 6. 新增通用模組流程

新增模組時，請在母體完成以下項目：

```text
module/backend/module-[key]/
module/frontend-web/src/features/[key]/
module/backend/module-system/src/main/resources/module-catalog.tsv
docs/module-catalog.md
```

後端至少包含：

- `pom.xml`
- `config`
- `controller`
- `dto`
- `entity`
- `repository`
- `service`
- `src/main/resources/db/migration/[key]`
- 單元測試或 service 測試

前端至少包含：

- feature API contract
- 型別定義
- 可被 portable bundle 匯出的入口
- 不綁死最終 UI 風格

清冊更新後需重新生成文件：

```bash
scripts/module-export.sh --all --format markdown > docs/module-catalog.md
```

再執行：

```bash
scripts/module-release-check.sh --modules [key]
```

若新模組依賴多個既有模組，也要用高依賴組合跑一次，例如：

```bash
scripts/module-release-check.sh --modules payroll,[key]
```

## 7. 禁止事項

- 禁止在目標專案直接覆蓋已客製化的前端 UI。
- 禁止直接複製整個母體倉庫到目標專案源碼根目錄。
- 禁止在目標專案的 `reference/模塊化組件/` 內直接修改並當成母體發布來源。
- 禁止讓目標專案使用 `source.dirty=true` 的 bundle 作為正式基線。
- 禁止只更新 `module-catalog.tsv` 卻不更新 `docs/module-catalog.md`。
- 禁止新增模組但不補 Flyway location、feature toggle 與導入驗證。
- 禁止在 `main/master` 直接開發母體模組。

## 8. 目標專案應記錄的內容

每次導入、追加或升級模組後，目標專案 devlog 或 PR 說明應記錄：

```text
母體來源 tag：
母體來源 commit：
requestedModules：
requiredModules：
manifest diff 摘要：
手動合併檔案：
驗證命令：
驗證結果：
是否有專案客製未回寫母體：
```

## 9. 回寫後發布基線

母體修正或新增模組完成後，使用以下順序：

```bash
git status --short --branch
scripts/module-release-check.sh --modules payroll
git add .
git commit -m "feat(modules): describe reusable change"
git push origin feature/module-leave
git tag -a module-vYYYY.MM.DD.N -m "module-vYYYY.MM.DD.N release baseline"
git push origin module-vYYYY.MM.DD.N
```

若只是文件或規範更新，也仍需跑 CI，並視是否要讓新專案基線包含該規範來建立新 tag。

## 10. 給其他 AI 的最小提示詞

```text
如果你在目標專案中修到通用模組 bug，或新增可重用模組，請先閱讀 reference/模塊化組件/ai-module-change-protocol.md。
專案專屬 UI 可留在目標專案；通用 API、DTO、Flyway、module key、feature toggle 必須回寫模塊化組件母體。
reference/模塊化組件 只能作為參考；正式回寫請切到真正母體 Git 倉庫，例如 /Users/wei/Desktop/code/模塊化組件。
回寫母體後必須跑 module-release-check.sh、commit、push，並建立新的 module-vYYYY.MM.DD.N tag。
```
