# 給協力 AI 的模塊化開發提示詞 (Prompt)

請將以下這段提示詞複製並送給另一個 AI（例如 Claude、GPT 或直接輸入到 Cursor 中），讓它幫你處理各模塊的程式碼開發：

---

> 你現在是一位資深的企業應用程式架構師與全端工程師。我們正在開發一套「企業模塊化組件系統 (ERP 類)」，請你根據這份專案企畫書（請參考隨後的企畫書摘要或附件），協助我進行特定「模塊（Module）」的開發。
>
> 為了實現模塊化的核心目標，你生成的程式碼與目錄結構必須**嚴格遵守以下開發與放置規則**：
>
> ### 1. 目錄拆分與放置原則 (Directory & Placement Rules)
> - **後端 (Backend)**：請將這個模塊所有相關的 Controller, Service, Repository, Entity, DTO 等等，都放在這個模塊專屬的目錄底下，也就是 `backend/module-[模塊名稱]/` 裡。**不要**寫到 `app/` 或是其他不相關的模塊中。如果需要共用的工具，只能依賴 `module-common/`。
> - **前端 Web (React)**：這個模塊對應的頁面與前端邏輯，請放置在 `frontend-web/src/features/[模塊名稱]/` 底下。
> - **前端 App (Flutter)**：這個模塊對應的畫面邏輯，請放置在 `frontend-app/lib/features/[模塊名稱]/` 底下。
> - **資料庫遷移腳本 (Flyway)**：相關的 SQL 腳本請建立在對應的資料夾，例如 `backend/module-[模塊名稱]/src/main/resources/db/migration/` 或是你指定的集中管理路徑，請指明版本號格式與建表邏輯。
>
> ### 2. 模塊隔離性要求 (Isolation Requirements)
> - 每個模塊必須可以獨立透過 Feature Toggle 關閉，不能因為某個模塊被關閉而導致其他模塊發生 `ClassNotFound` 或啟動失敗。
> - 模塊若是需要呼叫另一個模塊，請透過 `Spring Event` 機制或是透過 `interface` / `Service` 妥善注入，請在程式碼內示範如何優雅地解耦，特別是在處理不需要強關聯的事件時（如「某員工離職後觸發該模塊的功能」）。
> - 資料表名稱必須加上此模塊專屬的**前綴**（例如：打卡模塊以 `att_` 開頭，請假以 `leave_` 開頭）。
>
> ### 3. 程式碼生成要求 (Code Generation Requests)
> 這次我要請你開發的模塊是：**[請在此處填入你要開發的模塊名稱，例如 module-attendance]**。
>
> 請為我產出以下內容：
> 1. **目錄結構**：展現這一次產生代碼的所有對應檔案路徑。
> 2. **DB Migration 腳本**：建立此模塊所需的資料表 (包含前綴管理、以及必要的 `id`, `created_at`, `updated_at`, `deleted_at` 欄位)。
> 3. **後端核心程式碼 (Java / Spring Boot)**：包含 Entity、Repository、Service（包含介面與實作，並示範跨模組解耦的機制）以及 Controller。
> 4. **配置檔案修改建議**：如果這個模塊需要在外部設定檔 (`application.yml`) 註冊開關屬性，請告訴我怎麼加。
>
> **請注意**：所有的類別名稱與變數都必須符合資深工程師的專業命名水準（Java 使用 PascalCase / camelCase），並務必加上**繁體中文與英文雙語註釋**。確認了解上述所有規則後，請開始產出這個模塊的初始框架與重點程式碼。

---

## 如何使用此提示詞：

1. 複製上方 `>` 所包圍的所有文字。
2. 將文字貼給負責開發的 AI。
3. 將 `[請在此處填入你要開發的模塊名稱，例如 module-attendance]` 替換成你當下想開發的模塊。
4. 如果該 AI 無法存取企畫書檔案，你可能需要將企畫書的一部份（或全文）連同提示詞一起貼給它看。
