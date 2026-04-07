---
description: 模塊化組件開發與存放規範 (Modular Component Development Standards)
---

> [!IMPORTANT]
> 此規範專為「企業模塊化組件系統 (ERP 類)」制定，開發任何新的模塊時，必須**嚴格遵守**本文件所定義的目錄結構、隔離性要求與程式碼標準。

## 一、 核心原則 (Core Principles)

1.  **高度解耦 (High Decoupling)**: 模塊與模塊之間必須保持獨立。若模塊不被啟用（透過 Feature Toggle 關閉），不應導致整個應用程式無法啟動或產生 `ClassNotFound`、`BeanCreationException` 等錯誤。
2.  **單一職責與收斂 (Single Responsibility & Cohesion)**: 與該模塊相關的所有後端程式碼、前端頁面/組件、資料庫腳本，都必須存放在該模塊專屬的目錄之下，禁止散佈於系統各處。
3.  **共用依賴限制 (Common Dependency Limits)**: 若模塊需要使用通用的封裝與工具，只能依賴並調用 `module-common` (後端) 或 `shared` (前端) 定義好的內容。

## 二、 目錄拆分與放置原則 (Directory Structure)

### 1. 後端 (Backend - Java/Spring Boot)
所有特定模塊的程式碼一律放在 `backend/module-[模塊名稱]/` 中。

```
backend/module-[模塊名稱]/
├── src/main/java/com/enterprise/[模塊名稱]/
│   ├── controller/      # API 控制器 (需符合 RESTful 規範)
│   ├── service/         # 業務邏輯 (包含 interface 與 impl)
│   ├── repository/      # 資料存取層 (JPA/Hibernate)
│   ├── entity/          # 資料實體
│   ├── dto/             # 傳輸物件 (Request/Response)
│   ├── event/           # 該模塊發佈或監聽的事件
│   └── config/          # 該模塊專屬的設定
└── src/main/resources/
    └── db/migration/[模塊名稱]/  # (可選) 該模塊專屬的 DB Migration SQL
```
- **禁止事項**: 絕對不可以直接將特定業務模塊的程式碼寫在 `app/` (啟動模組) 中。

### 2. 前端 Web (React)
對應的頁面、局部組件與邏輯，放置在 `frontend-web/src/features/[模塊名稱]/`。

```
frontend-web/src/features/[模塊名稱]/
├── components/          # 專屬此模塊的局部組件
├── pages/               # 頁面視圖
├── api/                 # 針對此模塊的 API 調用封裝
├── hooks/               # 專屬 Hook
├── types/               # TypeScript 定義
└── index.ts             # 統一導出口
```

### 3. 前端 App (Flutter)
畫面邏輯放置於 `frontend-app/lib/features/[模塊名稱]/`

```
frontend-app/lib/features/[模塊名稱]/
├── screens/             # 畫面
├── widgets/             # 局部 Widget
├── providers/           # 狀態管理
└── models/              # 資料模型
```

## 三、 資料庫設計要求 (Database Requirements)

1.  **資料表名稱前綴**: 為了在單一資料庫中區分不同模塊的表，所有表名必須加上簡短的前綴。
    *   範例：打卡 `att_records`、請假 `leave_requests`、權限 `auth_users`。
2.  **必備通用欄位**: 除關聯表外，每張表必須包含：
    *   `id` (多數情況為 UUID 作為 PK)
    *   `created_at` (建立時間)
    *   `updated_at` (更新時間)
    *   `deleted_at` (供軟刪除使用)
3.  **無強外鍵約束 (建議)**: 若跨模塊關聯（例如打卡表關聯員工表），建議在實體層面使用 `employee_id` 記錄，而非在在資料庫層面建立強外鍵約束 (Foreign Key Constraint)，以提升彈性與遷移性。

## 四、 跨模塊通訊機制 (Inter-Module Communication)

由於模塊是在同一 JVM 中執行：

1.  **強依賴場景**: 如果模塊 A 運作**絕對需要**模塊 B 的資料（例如幾乎所有模塊都需要 `module-organization` 的員工資料），可以直接透過 Spring 的依賴注入調用模塊 B 的 `Service` 介面。
    ```java
    @Service
    public class LeaveServiceImpl implements LeaveService {
        private final EmployeeService employeeService; // 直接注入
        // ...
    }
    ```
2.  **弱依賴/非同步場景**: 如果模塊 A 發生某件事，只是想要「通知」其他模塊，請使用 **Spring Event (`ApplicationEventPublisher`)** 發佈事件，讓其他模塊實作 `@EventListener` 去監聽，達成解耦。
    *   例如：員工離職 (`module-organization`) 觸發取消未來打卡班表 (`module-attendance`) 及刪除系統權限 (`module-auth`)。

## 五、 當需要加入新模塊時的步驟清單 (Checklist)

當你要為專案新增一個 `module-XYZ`：

- [ ] 在 `backend` 的根 `pom.xml` 中加入此模塊為 `module`。
- [ ] 建立 `backend/module-XYZ` 目錄及標準結構。
- [ ] 該模塊的 `pom.xml` 中必定需要依賴 `module-common` (以及其他必須的前置模塊)。
- [ ] 若該模塊需要在 `app/` 的 `application.yml` 中新增開關設定，請在註解中說明清楚。
- [ ] 所有代碼遵循「代碼雙語註釋」及「中英 Git Commit」的規範。
