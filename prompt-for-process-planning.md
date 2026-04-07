# 給協力 AI 的流程規劃與測試提示詞 (Prompt)

請將以下這段提示詞複製並送給另一個 AI，讓它幫你規劃整個專案的開發流程、測試策略以及模塊整合方案：

---

> 你現在是一位資深的企業應用程式架構師、敏捷開發教練 (Scrum Master) 與測試總監 (QA Lead)。我們正在開發一套「企業模塊化組件系統 (ERP 類)」，這是一個由多個子模塊（如打卡、請假、財務、認證等）所組成的專案（後端為 Spring Boot Maven Multi-module；前端為 React/Flutter 的 Feature-based 架構）。
>
> 為了確保這套系統的品質與穩健性，請根據這份專案企畫書（請參考隨後的企畫書內容），為我打造一份詳盡的【開發與測試整合規劃書】。這份規劃書必須涵蓋以下三個核心面向：
>
> ### 1. 開發流程規劃 (Development Process Planning)
> *   **敏捷與迭代流程**：請為我們規劃適合模塊化專案的 Sprint 週期與交付目標。
> *   **分支管理與版本控制 (Branching Strategy)**：因應多模塊開發，我們該採用何種 Git Workflow？如何處理跨模塊的功能依賴 PR？
> *   **開發與 Code Review 準則**：為確保各模塊嚴格遵守「高內聚、低耦合」原則，Code Review 時應該特別檢查哪些關鍵點（例如：不可跨模塊直接呼叫 Repository, 是否依賴 Feature Toggle 等）？
>
> ### 2. 測試策略設計 (Testing Strategies)
> 此系統具備模塊獨立啟停的特性，請說明如何設計涵蓋整體的自動化與手動測試：
> *   **單元測試 (Unit Testing)**：針對後端 (JUnit/Mockito) 與前端 (Vitest/RTL) 的單元測試規範，哪些邏輯必須要有 80% 以上的覆蓋率？
> *   **整合測試 (Integration Testing)**：如何使用 Testcontainers 進行跨模塊通訊（如 Spring Event）的測試？
> *   **自動化 E2E 測試與 API 測試**：推薦的工具與實作流程。
>
> ### 3. 階段性模塊整合與穩定性保證 (Phased Integration & Stability)
> 這是最關鍵的部分。針對不同階段陸續開發出來的模塊（例如先有 `module-auth`，再加入 `module-leave` 與 `module-finance`）：
> *   **持續整合 (CI/CD) Pipeline**：如何在 CI 階段確保「加入新模塊後不想影響舊模塊」？
> *   **Feature Toggle (功能開關) 的灰度發布**：新模塊整合時，如何利用 Feature Toggle 機制在測試環境與正式環境進行安全驗證？
> *   **回歸測試 (Regression Testing)**：每當引入新模塊，應如何制定回歸測試範圍，避免破壞其他模塊的穩定性？
> *   **系統效能與異常隔離**：如果某個新模塊發生錯誤 (如 Memory Leak 或 Deadlock)，如何確保不導致同一 JVM 下的首要核心模塊崩潰？
>
> **要求**：請以專業的繁體中文，搭配清晰的標題、項目符號或表格，為我產出這份可落地的【開發與測試整合規劃書】。請著重於「模塊化」帶來的特殊挑戰與解法。

---

## 如何使用此提示詞：

1. 複製上方 `>` 所包圍的所有文字。
2. 將文字貼給負責協助你管理的 AI（如 Claude 或 ChatGPT）。
3. 如果該 AI 還不知道你的「企畫書」內容，請連同企畫書的全文一併提供給它，讓它能給出最切合你專案架構的規劃。
