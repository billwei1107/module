---
description: # 全局規則 (Global Rules)
---

# 全局規則 (Global Rules)

## 語言設定 (Language Protocols)

1. **思考與溝通 (Thinking & Communication)**: 所有的思考過程、任務清單 (Task List)、以及對用戶的回覆說明，一律使用 **繁體中文 (Traditional Chinese)**。
2. **註釋與文檔 (Comments & Documentation)**: 代碼註釋與功能文檔的語言規範，將依據各專案內的規則文件 (`.agent/rules` 或類似文件) 另行定義。

## 專案初始化流程 (Project Initialization Workflow)

初次建立或接觸新專案時，必須嚴格遵守以下順序：

1. **需求釐清 (Requirement Clarification)**: 詳讀並分析需求文檔，清楚定義開發的功能範圍。
2. **文檔整理 (Documentation Organization)**: 整理或建立完整的需求文檔。
3. **專案規範建立 (Setup Standards)**: 根據需求文檔，建立專案規則文件，並依據下方的日誌規範初始化日誌。

## 代碼開發規範 (Development Standards)

### 1. 命名規範 (Naming Conventions)

- **檔案與目錄 (Files & Directories)**:
  - **原則**:
    - 除非特定框架要求 (如 React Components, Java Classes)，否則**一律使用小寫**。
    - 禁止僅靠大小寫區別不同檔案 (如 File.txt 與 file.txt 不得並存)，以避免 macOS/Windows 檔案系統衝突。
  - 一般文件/專案目錄: 使用 **小寫** kebab-case (如 `user-profile.ts`, `utils/date-helper.js`, `devlog/troubleshooting.md`)。
  - 前端組件 (React/Vue): 使用 PascalCase (如 `UserProfile.tsx`)，確保檔名與組件名一致。
  - Java 類別檔: 使用 PascalCase (如 `OrderController.java`)。
- **變數與函數 (Variables & Functions)**:
  - JavaScript/TypeScript/Java/Go: 使用 camelCase (首字小寫，如 `getUserInfo`)。
  - Python: 使用 snake_case (全小寫，如 `get_user_info`)。
- **資料庫 (Database)**:
  - 資料表與欄位一律使用 **小寫** snake_case (如 `user_accounts`, `created_at`)，以避免跨平台資料庫大小寫敏感度問題。
- **常數與環境變數 (Constants & ENV)**:
  - 一律使用 **全大寫** UPPER_SNAKE_CASE (如 `MAX_RETRY_COUNT`, `DB_PASSWORD`)。

### 2. 前端開發 (Frontend)

- **硬編碼檢查**: 禁止在代碼中寫死 (Hardcode) 任何顯示文字或 API URL，應抽離至常數檔或 i18n 文件。
- **類型安全**: TypeScript 專案中禁止使用 `any`，必須定義明確的 Interface 或 Type。
- **組件設計**: 單一職責原則，過大的組件必須拆分為 Sub-components。

### 3. 後端開發 (Backend)

- **API 設計**: **嚴格遵守** RESTful API 設計規範，資源路徑使用名詞複數 (如 `GET /api/v1/users`)，並正確使用 HTTP Method (GET, POST, PUT, DELETE)。
- **錯誤處理**: 必須統一錯誤回傳格式 (Error Response Structure)，禁止直接將 Stack Trace 拋給前端。
- **分層架構**: 嚴格遵守 Controller -> Service -> Repository/DAO 的分層呼叫，禁止跨層存取。

### 4. 資料庫 (Database)

- **必備欄位**: 所有資料表 (除了關聯表) 應具備 id (PK), `created_at`, `updated_at`。
- **軟刪除**: 重要商業數據 (如訂單、用戶) 若無特殊原因，應使用 deleted_at 進行軟刪除 (Soft Delete)，而非實體刪除。
- **索引**: 針對 Foreign Key 和頻繁查詢欄位 (Where/Order By) 必須建立 Index。

### 5. 資安與敏感資料 (Security & Data Protection)

- **機密管理**: **絕對禁止**將密碼、API Key、Token 硬編碼與提交至版控系統。一律透過 環境變數 (Environment Variables) 注入。
- **個資保護**: Log 日誌中**嚴禁**輸出用戶密碼、信用卡號、身分證字號等敏感 PII 資訊。
- **密碼存儲**: 用戶密碼必須使用強雜湊演算法 (如 BCrypt, Argon2) 加鹽存儲，嚴禁明碼存儲。
- **SQL 注入**: 嚴禁組裝 SQL 字串，必須使用 ORM 或 Prepared Statement。

### 6. 環境變數 (.env)

- 所有專案根目錄必須包含 .env.example 範本文件，列出所有需要的變數名稱 (但不含真實機密值)。
- 應用程式啟動時，應檢查關鍵環境變數是否存在，若缺失應直接報錯停止啟動 (Fail Fast)。

## 版本控制規範 (Git & Version Control)

- **提交訊息 (Commit Message)**:
  - 格式: type(scope): subject
  - 常用 Type:
    - `feat`: 新功能
    - `fix`: 修補 Bug
    - `docs`: 文檔變更
    - `style`: 格式調整 (不影響代碼邏輯)
    - `refactor`: 重構 (無新功能或 Bug 修復)
    - `chore`: 建構工具或輔助任務
  - 範例: feat(auth): integrate jwt login flow
- **分支策略 (Branching)**:
  - main / `master`: 生產環境分支 (Production)，必須是穩定版本。
  - `develop`: 開發主分支，所有特性開發完成後合併至此。
  - `feature/xxx`: 功能開發分支 (如 `feature/user-login`)，完成後 PR 至 develop。
  - `fix/xxx`: Bug 修復分支。
- **忽略文件 (.gitignore)**:
  - 必須忽略: 當地環境設定 (`.env`), 依賴包 (`node_modules/`, `venv/`), 建構產物 (`dist/`, `build/`), 系統檔 (`.DS_Store`), IDE 設定 (`.idea/`, `.vscode/`)。

## Docker 與 部署規範 (Docker & Deployment Standards)

- **容器命名 (Container Naming)**:
  - 格式: [ProjectRootName]-[ServiceName]
  - 原則: 為避免多專案並行時的端口與名稱衝突，容器名稱必須包含專案根目錄名稱。
  - 範例: 若專案根目錄為 `pos-system`，則服務容器命名為 `pos-system-backend`, `pos-system-redis`, `pos-system-postgres`。
- **配置目錄結構 (Configuration Specifics)**:
  - Docker 配置: 分離本地與伺服器環境
    - 本地: docker/local/ (存放 `Dockerfile`, compose.yaml 等)
    - 伺服器: docker/server/
  - 環境變數 (.env):
    - 本地: env/local/
    - 伺服器: env/server/
- **配置原則 (Configuration Rules)**:
  - **禁止硬編碼**: Dockerfile 與 Compose 檔案中的所有敏感或可變配置（如 Port, Password, Version）必須使用環境變數 (`${VAR_NAME}`) 注入。
  - **環境變數對應**: 確保 env/ 目錄下的檔案能正確對應 Docker Compose 的 env_file 設定。

## 開發日誌規範 (Development Log)

- **檔案路徑**: 日誌必須存放於專案根目錄下的 devlog/ 資料夾中。
- **一般日誌**:
  - 命名格式: 使用 YYYY-MM-DD-devlog.md (如 `devlog/2026-01-08-devlog.md`)。
  - 時間確認: 每次創建或更新日誌前，必須確認當下系統時間 (`date`)。
  - 內容要求:
    1. **開發規劃 (Planning)**: 記錄當前的開發目標與待辦事項。
    2. **專案結構 (Project Structure)**: 必須記錄並更新當前的**整個專案目錄結構**，以及關鍵模組的說明。
    3. **進度 (Progress)**: 記錄已完成的工作項目。
- **問題追蹤 (Issue Tracking)**:
  - 命名格式: devlog/troubleshooting.md (遵循檔案命名規範，不使用中文檔名)。
  - 記錄時機: 只要遭遇 Error、Bug 或開發卡關時，**必須**即時記錄。
  - 記錄內容:
    1. **問題描述 (Issue)**: 包含錯誤代碼、Stack Trace 或異常行為描述。
    2. **解決方案 (Solution)**: 修復完成後，必須補上具體的解決方法、原因分析與代碼變更。

## 頁面結構文檔規範 (Page Structure Documentation Standards)

- **檔案路徑**: 頁面結構文檔應存放於專案的需求文檔目錄中 (如 `需求/頁面結構/`)，依照前台/後台分類。
- **檔案命名**: 使用描述性名稱 (如 `前台頁面結構.md`, `後台頁面結構.md`)。
- **圖表格式**: 使用 **Mermaid Flowchart** (`graph LR`) 語法繪製樹狀結構圖。
- **圖表方向**: 採用 **左到右 (LR)** 的樹狀展開方式，呈現層級關係。
- **節點層級標記**:

  - 每個節點必須添加對應層級的樣式類別標記 :::levelN
  - 範例: Main[前台主畫面]:::level1
- **樣式配色規範**:

  - **Level 1** (主節點): 橙橘色 `fill:#f97316`, 無外框 `stroke:none`, 白色文字 color:white
  - **Level 2** (第二層): 青綠色 `fill:#0d9488`, 無外框 `stroke:none`, 白色文字 color:white
  - **Level 3** (第三層): 紫色 `fill:#8b5cf6`, 無外框 `stroke:none`, 白色文字 color:white
  - **Level 4** (第四層): 粉色 `fill:#ec4899`, 無外框 `stroke:none`, 白色文字 color:white
- **樣式定義模板**:

  classDef level1 fill:#f97316,stroke:none,color:white;
  classDef level2 fill:#0d9488,stroke:none,color:white;
  classDef level3 fill:#8b5cf6,stroke:none,color:white;
  classDef level4 fill:#ec4899,stroke:none,color:white;
- **圖片連結**: 可使用 click 語法為節點添加圖片連結，方便快速查看詳細截圖。

  - 範例: click Main "前台主畫面.png"
- **註解說明**: 在圖表開頭添加說明文字，提示需使用支援 Mermaid 預覽的編輯器 (如安裝了 Mermaid 插件的 VS Code)。
- **限制事項**:

  - Mermaid 的 classDef 語法**不支援** font-family 和 font-weight 等字型屬性。
  - 字型樣式由編輯器或瀏覽器的預設設定控制。

## 代碼註釋規範 (Code Documentation Standards)

- **文件頭部註釋**:
  - 所有代碼文件開頭必須添加**中英文雙語註釋**，說明該文件的作用。
  - 格式範例 (TypeScript/JavaScript):

typescript
    /**
     * @file Login.tsx
     * @description 登入頁面組件 / Login page component
     * @description_en Handles user authentication with account, password, and employee ID
     * @description_zh 處理用戶登入驗證，包含帳號、密碼、員工編號
     */

- 格式範例 (CSS):

css
    /**
     * @file Login.css
     * @description 登入頁面樣式 / Login page styles
     * @description_en Defines styles for authentication form and layout
     * @description_zh 定義登入表單與頁面佈局樣式
     */

- **功能模塊註釋**:
  - 每個功能模塊 (函數、區塊、重要邏輯) 必須添加**中英文雙語註釋**。
  - 註釋應簡潔說明該段代碼的作用，便於 Debug 時快速檢索。
  - 格式範例:

typescript
    // ========================================
    // 時間格式化 / Time Formatting
    // ========================================
    const formatTime = (date: Date) => { ... };

    // ========================================
    // 登入處理 / Login Handler
    // ========================================
    const handleLogin = () => { ... };

- **目的**: 便於未來 Debug 時快速定位與檢索相關功能代碼。

## Git 提交記錄規範 (Git Commit Logging Standards)

- **記錄時機**: 每次 git commit 或 git push 後，必須在當日 devlog 中記錄提交資訊。
- **記錄格式**:

markdown

- **提交時間**: HH:MM
- **提交 Hash**: `xxxxxxx`
- **提交訊息**: `type(scope): description`

  - 變更摘要 1
  - 變更摘要 2
- **變更統計**: X 個檔案 (+N/-M)
- **記錄位置**: 在 devlog/YYYY-MM-DD-devlog.md 的「## Git 提交記錄」區塊中。
- **目的**: 追蹤每日開發進度，便於回溯與協作。

## 前端組件化規範 (Frontend Componentization Standards)

- **組件化原則**:

  - 所有可重用的 UI 元素（按鈕、輸入框、消息提示、彈窗等）必須抽離為獨立組件。
  - 組件存放於 src/components/ 目錄下，按功能分類建立子目錄。
  - 每個組件目錄應包含：組件檔 (`.tsx`)、樣式檔 (`.css`) 和匯出檔 (`index.ts`)。
- **目錄結構範例**:

  src/components/
  ├── button/
  │   ├── ActionButton.tsx
  │   ├── LoginButton.tsx
  │   └── index.ts
  ├── toast/
  │   ├── Toast.tsx
  │   ├── Toast.css
  │   └── index.ts
  └── input/
  ├── TextField.tsx
  └── index.ts
- **組件設計原則**:

  1. **單一職責**: 一個組件只負責一項功能。
  2. **可配置**: 透過 Props 提供必要的配置選項。
  3. **可重用**: 避免硬編碼，確保組件可在不同場景使用。
  4. **類型安全**: 使用 TypeScript 定義 Props 介面。
- **頁面與組件區分**:

  - **頁面 (Pages)**: 存放於 `src/pages/`，代表完整的頁面視圖。
  - **組件 (Components)**: 存放於 `src/components/`，為可重用的 UI 單元。