# AI 交接入口 / AI Handoff Guide

本文件是給其他 AI 的最短入口。使用者通常不會手動操作指令；AI 必須依照本文件自行閱讀、規劃、執行、驗證與回寫。

## 1. 目前定位

本資料夾是跨專案共用的模塊化組件母體。正式母體位置：

```text
/Users/wei/Desktop/code/模塊化組件/
```

遠端來源：

```text
billwei1107/module
```

當本資料夾出現在目標專案的 `reference/模塊化組件/` 時，它只能作為 AI 規劃參考與匯出工具來源，不是目標專案正式源碼，也不是正式回寫來源。

## 2. AI 必讀順序

請依序閱讀：

1. `ai-handoff.md`
2. `ai-module-planning.md`
3. `docs/module-catalog.md`
4. `docs/module-portability-guide.md`
5. `docs/module-release-guide.md`
6. `ai-module-change-protocol.md`

## 3. 新專案規劃流程

如果使用者正在建立新專案，請先輸出規劃，不要直接改程式碼。

規劃至少包含：

```text
需求摘要：
建議導入模組：
自動依賴：
暫不導入模組與原因：
導入風險：
建議導入順序：
待確認問題：
```

正式導入前先 dry-run：

```bash
bash reference/模塊化組件/scripts/module-export.sh --modules payroll
```

確認後正式匯入目標專案：

```bash
bash reference/模塊化組件/scripts/module-export.sh \
  --modules payroll \
  --target "$PWD" \
  --execute \
  --require-clean
```

導入後驗證：

```bash
bash reference/模塊化組件/scripts/module-verify-import.sh --target "$PWD"
```

## 4. 既有專案追加或升級模組

若目標專案已經有 `module/module-bundle-manifest.json`，追加或升級前必須先讀取 manifest：

```bash
python3 -m json.tool module/module-bundle-manifest.json
```

先匯出到暫存目錄：

```bash
bash reference/模塊化組件/scripts/module-export.sh \
  --modules payroll \
  --target /tmp/module-upgrade-check \
  --execute \
  --require-clean
```

比對差異：

```bash
bash reference/模塊化組件/scripts/module-manifest-diff.sh \
  --from module/module-bundle-manifest.json \
  --to /tmp/module-upgrade-check/module/module-bundle-manifest.json
```

確認後再正式匯入並驗證。

## 5. 回寫母體規則

如果變更只屬於單一專案，例如 UI、品牌文案、客戶專屬流程，留在目標專案。

如果變更屬於通用模組問題，例如 API contract、DTO、Flyway、module key、feature toggle、權限、稽核、薪資/財務/考勤計算邏輯，必須回寫真正母體：

```bash
cd /Users/wei/Desktop/code/模塊化組件
git checkout feature/module-leave
git pull origin feature/module-leave
```

修正後執行驗證：

```bash
scripts/module-release-check.sh --modules payroll --target /tmp/module-release-check-payroll
```

通過後提交與推送：

```bash
git status --short --branch
git add .
git commit -m "fix(payroll): describe reusable fix"
git push origin feature/module-leave
```

若要讓其他專案正式引用，建立新 tag：

```bash
git tag -a module-vYYYY.MM.DD.N -m "module-vYYYY.MM.DD.N release baseline"
git push origin module-vYYYY.MM.DD.N
```

## 6. 禁止事項

- 禁止把 `reference/模塊化組件/` 當成目標專案正式源碼。
- 禁止在 `reference/模塊化組件/` 內直接修改並當成母體發布。
- 禁止只修目標專案而不回寫通用 bug。
- 禁止使用 `source.dirty=true` 的 bundle 作為正式基線。
- 禁止在 `main/master` 直接開發母體。

## 7. 使用者可直接貼給 AI 的提示詞

```text
請先閱讀 reference/模塊化組件/ai-handoff.md。
我基本不手動操作指令，請你依文件自行規劃、執行、驗證與記錄。
reference/模塊化組件 只能作為規劃參考與匯出工具來源，不要直接當正式源碼修改。
若開發中發現通用模組 bug 或新增可重用能力，請依 ai-module-change-protocol.md 回到真正母體 /Users/wei/Desktop/code/模塊化組件 修改、測試、commit、push，必要時建立新 module tag。
```
