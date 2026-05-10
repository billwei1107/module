# AI 模組規劃入口 / AI Module Planning Guide

本文件是給 AI 在新專案規劃階段先閱讀的入口。它用來判斷「新專案可能需要哪些模組」，不是用來直接複製整個母體倉庫。

若使用者只是把本資料夾丟進其他專案並要求 AI 接手，請先閱讀根目錄 `ai-handoff.md`，再依本文件進行模組規劃。

## 1. 使用定位

本倉庫是跨專案共用的模塊化組件母體，已從 POS 專案根目錄獨立出來。母體實際 clone 路徑可依開發機環境調整；目前建議位置為：

```text
/Users/wei/Desktop/code/模塊化組件/
```

其他專案若需要讓 AI 在規劃階段參考模組，請把母體以 clone、submodule 或複製 release tag 的方式放在目標專案的 `reference/模塊化組件/`。該 reference 只作為規劃與匯出工具來源，不是正式業務源碼。

當新專案旁邊放有本母體倉庫，例如：

```text
ERP/
├── project-template/
├── reference/
│   └── 模塊化組件/
└── ai-task.md
```

AI 應把 `reference/模塊化組件/` 視為模組母體與規劃參考，不是 ERP 專案源碼。正式導入時必須使用 `scripts/module-export.sh` 產生 portable bundle。

## 2. AI 必讀順序

新專案規劃前請依序閱讀：

1. `ai-handoff.md`：先理解 AI 接手、匯入與回寫的最短流程
2. `ai-module-planning.md`：先理解模組地圖與常見組合
3. `docs/module-catalog.md`：確認完整模組、依賴、後端/前端/Flyway 路徑
4. `docs/module-portability-guide.md`：確認匯出與導入流程
5. `docs/module-release-guide.md`：確認正式基線、tag 與驗證要求
6. `ai-module-change-protocol.md`：確認開發中修正或新增模組時如何回寫母體

## 3. 規劃原則

- 不要直接把整個母體倉庫複製進新專案源碼。
- 先根據需求選出候選模組，再用 `scripts/module-export.sh` 展開依賴。
- 前端 UI 可依新專案設計重做；API contract、DTO、module key、Flyway migration、feature toggle 語意不可任意改。
- 目標專案若已有 `module/module-bundle-manifest.json`，新增模組前必須先讀取既有 manifest。
- 正式導入基線應使用最新 release tag；目前基線為 `module-v2026.05.10.3`。

## 4. 模組地圖

### 基礎必備

| 模組 | 適用場景 | 備註 |
|---|---|---|
| `auth` | 登入、JWT、使用者與權限 | 多數專案必備 |
| `organization` | 公司、部門、職位、員工 | ERP/內部管理必備 |
| `workflow` | 審批流程、待辦、流程狀態 | 請假、財務、簽核類常用 |
| `notification` | 通知、WebSocket、站內訊息 | 專案、會議、公告類常用 |

### 人資與行政

| 模組 | 適用場景 | 主要依賴 |
|---|---|---|
| `attendance` | 打卡、考勤、工時、加班 | auth, organization |
| `leave` | 請假、假別、配額、代理人、簽核 | auth, organization, workflow, attendance |
| `payroll` | 薪資、計薪規則、薪資單 | auth, organization, attendance, leave, finance |
| `meeting` | 會議室、會議預約、會議紀錄 | auth, organization, notification |
| `announcement` | 公告、排程發布、已讀追蹤 | auth, organization, notification |

### 財務與營運

| 模組 | 適用場景 | 主要依賴 |
|---|---|---|
| `finance` | 會計科目、傳票、應收應付、預算 | auth, organization, workflow |
| `inventory` | 庫存、入出庫、盤點、安全庫存 | auth, organization |
| `crm` | 客戶、商機、銷售漏斗 | auth, organization |
| `project` | 專案、任務、看板、時間追蹤 | auth, organization, notification |
| `document` | 文件、版本、分享、標籤 | auth, organization |
| `report` | 報表、Dashboard、分析輸出 | auth, organization |
| `audit` | API 稽核、資料異動紀錄 | auth |
| `system` | Feature Toggle、系統設定、資料字典 | auth |

## 5. 常見新專案組合

### ERP 企業內部管理

建議先規劃：

```text
auth, organization, workflow, notification, attendance, leave, finance, payroll, audit, system, report
```

若需要庫存或客戶資料，再追加：

```text
inventory, crm, document
```

### POS / 門市營運

建議先規劃：

```text
auth, organization, inventory, finance, report, audit, system, announcement
```

若有員工排班或薪資，再追加：

```text
attendance, leave, payroll
```

### CRM / 業務管理

建議先規劃：

```text
auth, organization, crm, report, document, notification, audit
```

若商機流程需要簽核，再追加：

```text
workflow
```

### 專案協作平台

建議先規劃：

```text
auth, organization, project, notification, document, report, audit
```

若需要簽核或會議室，再追加：

```text
workflow, meeting
```

## 6. 規劃輸出格式

AI 在動手匯出前，應先輸出以下規劃：

```text
需求摘要：
- ...

建議導入模組：
- ...

自動依賴：
- ...

暫不導入模組與原因：
- ...

導入風險：
- 資料庫 migration：
- 既有前端 UI：
- 權限/角色：
- Docker/env：

建議導入順序：
1. ...
2. ...

待確認問題：
- ...
```

## 7. 正式匯出流程

先看 dry-run：

```bash
scripts/module-export.sh --modules payroll
```

確認後正式匯出：

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

若是既有專案升級或追加模組，先依 `ai-module-change-protocol.md` 讀取目標專案 manifest 並做差異比對。

## 8. 給其他 AI 的最小提示詞

```text
請先閱讀 reference/模塊化組件/ai-handoff.md，再閱讀 ai-module-planning.md。
reference/模塊化組件 是模組母體，只能參考與匯出模組，不要整包複製進專案源碼。
請根據新專案需求規劃需要導入哪些模組，先輸出規劃與風險，不要直接修改程式碼。
選定模組後，使用 module-export.sh 產生 bundle，並用 module-verify-import.sh 驗證。
```
