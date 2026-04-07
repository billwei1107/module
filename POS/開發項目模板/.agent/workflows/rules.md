---
description: 創勝搬家 ERP 核心開發規範 (包含全域 mainrule 與專案 projectrule)
---

// turbo-all

> [!IMPORTANT]
> 本文件為核心執行指南，包含全局（mainrule）與專案特定（projectrule）規範。作為 Antigravity，在處理此專案時必須**自動且始終**遵循以下規則，無需用戶手動觸發。

## 一、 語言與通訊規範 (Language & Communication)

1.  **全面繁體中文**：所有的對話、任務報告、文檔撰寫一律使用繁體中文。
2.  **代碼雙語註釋**：
    - 以模塊、函式為單位撰寫註釋。
    - 必須包含 **[繁體中文 - English]** 雙語對照。
    - 格式：
      ```java
      /**
       * 獲取用戶列表 - Get user list
       * 根據查詢條件返回用戶列表 - Returns list based on query
       */
      ```
3.  **Git 提交訊息**：採取 `[類型]: 描述` 格式，中英文混合（如：`feat: 增加登入功能 Add login function`）。

## 二、 開發日誌系統 (Devlog System - 核心)

必須在 `devlog/` 目錄下維護以下文件：
1.  **一般日誌** (`devlog/YYYY-MM-DD-devlog.md`)：
    - **開發規劃 (Planning)**: 列出當前目標與待辦事項。
    - **專案結構 (Project Structure)**: 記錄專案目錄結構。
    - **任務記錄區塊 (Task Logs)**: 核心記錄方式，取代舊有的進度與提交分離模式。
      - **標題格式**: `## [任務標題] [HH:MM]` (如 `## 估價單功能優化 [14:00]`)。
      - **時間準確性**: 必須在記錄前執行 `date` 指令確認當下時間，禁止猜測或估算。
      - **內容要求**:
        - `### 完成功能 / 修復內容`: 詳細列出前端/後端的具體變更點。
        - `### 提交內容`: 列出相關的 Git Commit Hash 與 Message。
2.  **問題追蹤** (`devlog/troubleshooting.md`): 記錄開發遇到的坑與解決方案。

## 三、 技術與架構規範 (Technical Specs)

1.  **技術棧**：
    - 後端：Spring Boot 3.x (Java 21), PostgreSQL, Redis.
    - 前端 (Web)：React + TypeScript, Material-UI, Vite.
    - 前端 (App)：Flutter.
2.  **RESTful API**：資源導向、統一響應格式、版本控制 (v1)。
3.  **路徑規範**：
    - **禁止硬編碼絕對路徑**。
    - 訪問路徑必須透過環境變數傳入或使用相對項目根目錄的路徑。

## 四、 需求與流程圖管理 (Requirement & Flowcharts)

1.  **統一存放**：需求文檔存放於 `需求/`，流程圖與圖片存放於 `需求/流程圖/`。
2.  **流程圖標準**：
    - 使用 Mermaid 或 Markdown 友善語法。
    - **背景色一律為白色 (-b white)**。
    - 匯出圖片參數：`-b white -w 7680 -H 4320 -s 4`。

## 五、 自動化動作 (Automation Tasks)

1.  **任務啟動前**：先建立繁體中文任務清單。
2.  **時間確認**：撰寫代碼前必先獲取當前時間（%date/或系統時間）並對照期限。
3.  **任務完成後**：自動更新 Devlog 並執行 Git 提交（若環境支援）。

---
*本文件整合自 .cursor/rules/mainrule.mdc 與 .cursor/rules/projectrule.mdc*
