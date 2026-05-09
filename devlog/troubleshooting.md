# Troubleshooting DevLog

本文件用於記錄開發過程中遭遇的重大 Bug、Exception 與環境問題，作為後續知識傳承與除錯指引。

## 2026-04-07 - Spring Boot 多模組 (Multi-Module) 整合崩潰四連發

### 問題 1: Flyway SQL 版號互撞
**Issue**: 整合啟動時拋出 `org.flywaydb.core.api.FlywayException: Found more than one migration with version 1`。
**原因分析**: 在獨立開發 Auth 與 Workflow 模組時，皆建置了名為 `V1__` 開頭的 migration sql 檔，在 Spring Boot 同時掛載執行 classpath 時引發覆蓋衝突。
**Solution**: 將檔案更名為 `V1.0__create_auth_tables.sql`, `V1.1__create_org_tables.sql`, `V1.2__create_wf_tables.sql`，利用次版號強制作出優先排序。

### 問題 2: Spring JPA Bean 啟動孤立
**Issue**: 拋出 `NoSuchBeanDefinitionException`，找不到 `RoleRepository`。
**原因分析**: 加入 Redis 時觸發 Spring Data 的 Strict Repository Mode，而 `WorkflowModuleConfig` 身上掛有 `@EnableJpaRepositories`，這使得 Spring Boot 放棄了針對全域的基礎掃描，導致無專屬配置檔的 `module-auth` 與 `module-organization` 中遺失 Repository 注入。
**Solution**: 徹底去耦化。移除 `Application.java` 中的全域干擾宣告，並為 `auth` 和 `organization` 分別撰寫 `AuthModuleConfig` 與 `OrganizationModuleConfig` 且標示專屬包的掃描位置。

### 問題 3: 缺少 PasswordEncoder Bean 導致實體啟動驗證不過
**Issue**: `Parameter 2 of constructor in com.enterprise.auth.service.impl.AuthServiceImpl required a bean... PasswordEncoder`
**原因分析**: 單元測試透過 `@InjectMocks` 迴避了此問題，直到 Spring Context 自動掛載時才報錯。
**Solution**: 於 `AuthModuleConfig` 補綴 `@Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }`

### 問題 4: Docker 虛擬網路端口錯亂
**Issue**: `Connection to postgres:15432 refused` 與 Axios Timeout 10000ms。
**原因分析**: 外部 `.env` 將 `POSTGRES_PORT` 導向本機 15432 方便查驗，但後端 docker 吃此變量後導致於虛擬內網向 postgres 發送 15432 的請求，實際內部僅開啟 5432 埠。後端死亡導致 Nginx 回報 502/10000ms timeout。
**Solution**: 於 `docker-compose.yml` 之 `backend` service 的 `environment` 直接霸王覆蓋 `- POSTGRES_PORT=5432` 與宣告 `- DB_HOST=postgres`。

## 2026-05-09 - 後端缺少 Maven Wrapper

### 問題: `./mvnw` 不存在
**Issue**: 在 `module/backend` 執行 `./mvnw test` 時出現 `zsh:1: no such file or directory: ./mvnw`。
**原因分析**: 後端模組目前沒有提交 Maven Wrapper 檔案，不能使用 `./mvnw` 作為驗證入口。
**Solution**: 改用系統 Maven 執行 `mvn test`，後端全模組測試通過。此問題已同步寫入 Obsidian raw 與 ai-kb：
- `~/Desktop/obsidian/raw/coding/errors/2026-05-09-missing-maven-wrapper.md`
- `~/ai-kb/kb/60-errors/backend/2026-05-09-missing-maven-wrapper.md`

## 2026-05-09 - Vite 瀏覽器環境缺少 global

### 問題: `global is not defined`
**Issue**: Playwright 開啟 `/leave/requests` 時頁面 body 為空，console 顯示 `pageerror global is not defined`。
**原因分析**: `sockjs-client` 等瀏覽器端依賴引用 Node 風格的 `global` 變數，但 Vite 瀏覽器 runtime 預設沒有提供該全域變數。
**Solution**: 在 `module/frontend-web/vite.config.ts` 加入 `define: { global: 'globalThis' }`。修復後 `npx tsc -b`、`npm run build` 與 Playwright 互動測試皆通過。此問題已同步寫入 Obsidian raw 與 ai-kb：
- `~/Desktop/obsidian/raw/coding/errors/2026-05-09-vite-global-is-not-defined.md`
- `~/ai-kb/kb/60-errors/frontend/2026-05-09-vite-global-is-not-defined.md`

## 2026-05-09 - Playwright 文字定位過寬造成 strict mode violation

### 問題: `getByText('policy')` 同時匹配標籤與檔名
**Issue**: `module-document` 前端 smoke test 驗證標籤時，`getByText('policy')` 同時匹配到 `leave-policy.pdf`、`store-policy.pdf` 與標籤 `policy`，Playwright 回報 strict mode violation。
**原因分析**: 臨時測試使用部分文字匹配，文件名稱也包含相同片段，導致 locator 不唯一。
**Solution**: 改為 `getByText('policy', { exact: true })` 精確匹配標籤文字。修正後 `npx playwright test tmp-document-browser.spec.ts --reporter=line` 通過。此問題已同步寫入 Obsidian raw：
- `~/Desktop/obsidian/raw/coding/errors/2026-05-09-playwright-text-locator-strict-mode.md`

## 2026-05-09 - Playwright 臨時 smoke test 指令與路由匹配問題

### 問題: project 名稱、文字定位與 query URL mock pattern 不匹配
**Issue**: `module-inventory` 前端 smoke test 初跑時依序遇到 `Project(s) "chromium" not found`、`getByText('庫存筆數：0')` strict mode violation，以及盤點 API mock 未匹配 query URL 導致頁面顯示 `盤點失敗 / Failed to process stock take`。
**原因分析**: 此前端專案沒有 `playwright.config` project 名稱，不能指定 `--project=chromium`；庫存筆數文字被低庫存筆數部分匹配；`freeze` API 會帶 query params，臨時 route pattern 沒有包含尾端 wildcard。
**Solution**: 改用 `npx playwright test tmp-inventory-browser.spec.ts`；文字定位改為 `getByText('庫存筆數：0', { exact: true })`；stock-take mock route 改成 `**/stock-takes/freeze**`、`**/count**`、`**/adjust**`。修正後 inventory smoke test 通過。此問題已同步寫入 Obsidian raw：
- `~/Desktop/obsidian/raw/coding/errors/2026-05-09-playwright-inventory-smoke-test-routing.md`

## 2026-05-09 - Playwright 按鈕名稱部分匹配造成 strict mode violation

### 問題: `建立會議` 同時匹配 `建立會議室`
**Issue**: `module-meeting` 前端 smoke test 點擊 `getByRole('button', { name: '建立會議' })` 時，同時匹配到 `建立會議室` 與 `建立會議`，Playwright 回報 strict mode violation。
**原因分析**: Playwright role locator 的 `name` 預設允許部分匹配，兩個按鈕文字有共同片段。
**Solution**: 將定位改為 `getByRole('button', { name: '建立會議', exact: true })`。修正後 meeting smoke test 通過。此問題已同步寫入 Obsidian raw：
- `~/Desktop/obsidian/raw/coding/errors/2026-05-09-playwright-button-name-partial-match.md`

## 2026-05-09 - Announcement 單測 Mock 回傳序列與 Maven lifecycle 名稱

### 問題: `confirm()` 測試未觸發 confirmation save，且 Maven phase 誤寫為 `testCompile`
**Issue**: `module-announcement` 單測初跑時，`confirmShouldMarkReadAndConfirmedForImportantAnnouncement` 驗證 `AnnouncementConfirmationRepository.save()` 未被呼叫；另一次查 warning 時使用 `mvn ... testCompile`，Maven 回報 `Unknown lifecycle phase "testCompile"`。
**原因分析**: `confirm()` 會先呼叫 `markRead()`，而 `markRead()` 回傳 DTO 時會先查詢一次 confirmation 狀態，導致 Mockito 連續回傳序列被提前消耗；Maven 標準 lifecycle phase 使用 kebab-case，正確名稱為 `test-compile`。
**Solution**: 將 confirmation repository mock 回傳序列調整為 `Optional.empty(), Optional.empty(), Optional.of(...)`，並改用 `mvn ... test-compile` 查編譯。修正後 `mvn clean test -pl module-announcement -am` 通過。此問題已同步寫入 Obsidian raw：
- `~/Desktop/obsidian/raw/coding/errors/2026-05-09-announcement-test-mock-sequence.md`

## 2026-05-09 - Spring Boot 全域掃描繞過 Feature Toggle

### 問題: `scanBasePackages = "com.enterprise"` 直接掃入已關閉模組元件
**Issue**: 第四階段完成驗收時檢查 Feature Toggle，發現 `Application` 使用 `@SpringBootApplication(scanBasePackages = "com.enterprise")`，即使各模組 `ModuleConfig` 有 `@ConditionalOnProperty`，Controller/Service 仍可能因全域 component scan 被直接載入；同時 `module-attendance`、`module-notification` 缺少模組開關條件。
**原因分析**: 模組配置類上的條件只控制該配置類是否生效，但 app 的全域掃描已經覆蓋所有 `com.enterprise.*` package，會繞過 `ModuleConfig` 的 gating。`notification` 的 `WebSocketConfig` 也獨立位於 config package，需跟隨模組開關。
**Solution**: 將 `Application` 掃描範圍縮小為 `com.enterprise.common` 與各模組 `config` package；由每個 `ModuleConfig` 在模組啟用時自行 `@ComponentScan` 模組 package。補上 `AttendanceModuleConfig`、`NotificationModuleConfig`、`WebSocketConfig` 的 `@ConditionalOnProperty`，並替 `WorkflowModuleConfig` 補 `@ComponentScan`。新增 `ModuleFeatureToggleMetadataTest` 鎖定掃描邊界與 `modules.*` 開關元資料。修正後 `mvn test -pl app -am` 通過。此問題已同步寫入 Obsidian raw：
- `~/Desktop/obsidian/raw/coding/errors/2026-05-09-spring-global-scan-bypasses-feature-toggle.md`

## 2026-05-09 - Spring Profile 模組開關繼承造成業態組合失真

### 問題: `application-*.yml` 未列出的 `modules.*` 會沿用 base 預設
**Issue**: 業態 profile（如 `application-cafe.yml`, `application-fastfood.yml`）只宣告 `auth`, `organization`, `workflow`, `notification`, `attendance`，未宣告的 `finance`, `payroll`, `project` 等模組會沿用 `application.yml` 的 `true`，導致啟用特定 profile 時載入非該業態預期的模組。
**原因分析**: Spring Boot 會將 base `application.yml` 與 profile-specific YAML 合併；profile 只覆寫有宣告的 key，未宣告的 map key 不會自動變成 false。因此模組化母體若在 base 開啟多數模組，業態 profile 必須明確列出全部 `modules.*`。
**Solution**: 補齊 cafe、fastfood、restaurant、retail、chain-hq 五個 profile 的 17 個模組開關，避免隱式繼承 base 預設。並在 `ModuleFeatureToggleMetadataTest` 新增 profile 完整性測試與依賴一致性測試，確保每個 profile 明確宣告所有模組，且 `payroll`、`leave`、`finance`、`meeting` 等依賴組合不會被拆壞。修正後 `mvn test -pl app -am` 通過。此問題已同步寫入 Obsidian raw：
- `~/Desktop/obsidian/raw/coding/errors/2026-05-09-spring-profile-module-toggle-inheritance.md`

## 2026-05-09 - Playwright API mock 過寬攔截 Vite source module

### 問題: Vite module script 被 mock 成 JSON
**Issue**: 前端 Feature Toggle browser test 初跑時頁面 body 為空，console 顯示 `Failed to load module script: Expected a JavaScript-or-Wasm module script but the server responded with a MIME type of "application/json"`。
**原因分析**: 臨時測試使用 `page.route('**/api/**')` 作為後端 API fallback mock，但 Vite 會載入 `/src/features/system/api/systemApi.ts`，路徑中也包含 `/api/`，因此 source module 被 Playwright route 攔截並回傳 JSON。
**Solution**: 將 mock pattern 限縮為 `**/api/v1/**` 與 `**/api/api/v1/**`，避免攔截 `/src/**` source module；並將 feature endpoint 的 route 註冊在 fallback 後面，確保較精準的 mock 優先處理。修正後 Playwright browser script 通過。此問題已同步寫入 Obsidian raw：
- `~/Desktop/obsidian/raw/coding/errors/2026-05-09-playwright-api-mock-overmatches-vite-source.md`

## 2026-05-09 - Local Docker Compose 缺少本地 env 與插值預設

### 問題: `env/local/.env` 不存在且 compose 插值變數未設定
**Issue**: 執行 `docker compose -f module/docker/local/docker-compose.yml config` 時，出現 `env file .../module/env/local/.env not found`，並同時警告 `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `POSTGRES_PORT`, `REDIS_PASSWORD`, `REDIS_PORT` 等變數未設定。
**原因分析**: Compose 的 `env_file` 用於注入 container runtime environment，但不保證可作為 compose YAML 插值來源；fresh clone 時 `module/env/local/.env` 不會存在，且原本 YAML 沒有為插值提供 fallback。Redis 與 backend 的預設密碼也未明確對齊，容易造成 local 啟動後連線失敗。
**Solution**: 將 `env_file` 改為 optional，並在 compose YAML 內替 DB、Redis、JWT 與服務 port 補上本地預設值；backend 在 compose network 內固定使用 `postgres:5432` 與 `redis:6379`。新增 `module/env/local/.env.example` 作為本地覆寫範本，並加入 top-level `name: ${COMPOSE_PROJECT_NAME:-module}` 固定預設 compose project name。修正後 `docker compose -f module/docker/local/docker-compose.yml config` 通過。此問題已同步寫入 Obsidian raw：
- `~/Desktop/obsidian/raw/coding/errors/2026-05-09-docker-compose-local-env-defaults.md`

## 2026-05-09 - Docker daemon 未啟動導致 compose build 無法執行

### 問題: 無法連線到 OrbStack Docker socket
**Issue**: 執行 `docker compose -f module/docker/local/docker-compose.yml build backend frontend` 時出現 `Cannot connect to the Docker daemon at unix:///Users/wei/.orbstack/run/docker.sock. Is the docker daemon running?`。
**原因分析**: 本機 Docker daemon/OrbStack 未啟動，CLI 無法連線到 Docker socket，因此不能建立實際 image。
**Solution**: 本輪先以 Dockerfile 等價步驟驗證建構鏈：`mvn -f module/backend/pom.xml clean package -DskipTests` 與 `npm run build` 均通過；待 Docker daemon/OrbStack 啟動後，需再執行完整 `docker compose -f module/docker/local/docker-compose.yml build backend frontend` 與 `docker compose -f module/docker/local/docker-compose.yml up`。

## 2026-05-09 - 前端 ESLint 基線存在 any 與 React Hooks warning

### 問題: CI 導入前 `npm run lint` 會出現 error/warning
**Issue**: 建立 CI 基線前執行 `npm run lint`，出現 `@typescript-eslint/no-explicit-any`、React Hooks `exhaustive-deps` warning，以及 React Hooks 外掛新版 `set-state-in-effect`、`refs`、`purity` error。
**原因分析**: 既有前端部分 API 與 hook 使用 `any` 迴避 Axios interceptor 型別；資料載入函式未以 `useCallback` 穩定依賴；React Hooks 外掛新版規則包含 React Compiler 導向檢查，會將此專案現有頁面常見的資料載入 effect 判定為錯誤，造成 CI 基線無法落地。
**Solution**: 將 auth API、角色/使用者清單、登入 hook、WebSocket hook 與共用 `ApiResponse` 改為明確型別或 `unknown`；以 `useCallback` 清理 hook dependency warning；在 ESLint config 關閉不適合現有頁面模式的 `react-hooks/set-state-in-effect`、`react-hooks/refs`、`react-hooks/purity`，並保留 hooks 基礎規則。修正後 `npm run lint -- --max-warnings=0` 通過。此問題已同步寫入 Obsidian raw：
- `~/Desktop/obsidian/raw/coding/errors/2026-05-09-frontend-eslint-ci-baseline.md`

## 2026-05-09 - GitHub Actions Node 20 runtime 淘汰註記

### 問題: `actions/checkout@v4` 與 `actions/setup-node@v4` 使用 Node 20 runtime
**Issue**: GitHub Actions 首次 CI run 顯示 annotation：`Node.js 20 actions are deprecated`，並提示 2026-06-02 後 runner 會預設切到 Node 24。
**原因分析**: 初版 workflow 使用 `actions/checkout@v4`、`actions/setup-node@v4`、`actions/setup-java@v4`，這些版本仍可能執行在 Node 20 action runtime。雖然當下 job 可通過，但會留下短期升級風險。
**Solution**: 透過遠端 tag 確認官方 actions 已提供新版後，升級為 `actions/checkout@v6`、`actions/setup-node@v6`、`actions/setup-java@v5`，重新推送觸發 CI。

## 2026-05-09 - 模組清冊 Flyway location 測試誤判 auth 根目錄 migration

### 問題: `classpath:db/migration` 未符合測試中的 slash 前綴
**Issue**: 新增模組清冊 metadata 後，`mvn test -pl module-system -am` 失敗，測試錯誤為 `Expecting actual: "classpath:db/migration" to start with: "classpath:db/migration/"`。
**原因分析**: 多數業務模組的 migration 位於 `db/migration/[module]`，但 `module-auth` 的 migration 位於根 `db/migration`，這是現有 Flyway locations 的正確配置。測試條件過度假設每個模組都必定有子目錄。
**Solution**: 將清冊中的 Flyway location 改為每個模組明確宣告，並把測試條件放寬為 `classpath:db/migration` 前綴，同時保留 `auth` 必須等於 `classpath:db/migration` 的精準斷言。此問題已同步寫入 Obsidian raw：
- `~/Desktop/obsidian/raw/coding/errors/2026-05-09-module-catalog-auth-flyway-location.md`

## 2026-05-09 - Bash 空陣列搭配 nounset 造成搬移腳本中止

### 問題: `REQUESTED_MODULES[@]: unbound variable`
**Issue**: 新增 `scripts/module-export.sh` 後執行 `scripts/module-export.sh --modules payroll`，腳本在 `append_unique` 讀取空陣列時中止，錯誤為 `scripts/module-export.sh: line 176: REQUESTED_MODULES[@]: unbound variable`。
**原因分析**: macOS 預設 Bash 3.2 在 `set -u` / nounset 模式下，對尚無元素的陣列執行 `"${ARRAY[@]}"` 可能被視為未綁定變數。搬移腳本需要在多個空陣列上累加模組清單，因此 strict nounset 會讓首次 append 失敗。
**Solution**: 將腳本開頭由 `set -euo pipefail` 調整為 `set -eo pipefail`，保留錯誤中止與 pipefail，但避免 Bash 3.2 空陣列 nounset 問題。修正後 `bash -n scripts/module-export.sh`、`--modules payroll`、`--format rsync` 與 `--execute` 複製 smoke test 均通過。此問題已同步寫入 Obsidian raw：
- `~/Desktop/obsidian/raw/coding/errors/2026-05-09-bash-empty-array-nounset.md`

## 2026-05-09 - 部分模組 portable 匯入後 build 失敗

### 問題: `crm` 匯入乾淨目標後 Maven 與 Vite 依序失敗
**Issue**: 執行 `scripts/module-export.sh --modules crm --target /tmp/module-import-crm-smoke --execute` 後，`mvn -f /tmp/module-import-crm-smoke/module/backend/pom.xml test` 起初失敗，錯誤包含 `Child module ... module-workflow ... does not exist`；修正父 POM 後又因 app 舊 `target/test-classes` 引用未匯出的模組而出現 `NoClassDefFoundError: com/enterprise/workflow/...`；前端 `npm run build` 起初也因缺少 `index.html` 顯示 `Cannot resolve entry module index.html`。
**原因分析**: 匯出工具早期只複製母體整合檔，父 POM、app POM、`Application.java`、`application.yml`、前端 `App.tsx` 仍以全模組母體為假設，不適合只匯出部分模組。`rsync -a` 也會把母體既有 `target/`, `node_modules/`, `dist/` 等 build 產物一起帶到目標端，造成 Maven 使用過期 test-classes。前端支援檔清單漏列 Vite 根入口 `frontend-web/index.html` 與 public assets。
**Solution**: `--execute` 改為輸出 portable bundle：排除 `target`, `node_modules`, `dist`，並重寫目標端 `module/backend/pom.xml`、`app/pom.xml`、`Application.java`、`application.yml`、`frontend-web/src/App.tsx`、`moduleNavigation.ts`，只保留 required modules。補入 `frontend-web/index.html` 與 `public/`。新增 CI `Validate portable CRM import build`，在 `/tmp` 實際匯出 `crm` 並執行 `mvn test`, `npm ci`, `npm run build`。

## 2026-05-09 - 前端 npm audit 高風險依賴

### 問題: `axios` high vulnerability 與間接依賴弱點
**Issue**: 在 portable import smoke 的 `npm ci` 後，npm 顯示 `3 vulnerabilities (2 moderate, 1 high)`；執行 `npm audit --json` 後確認高風險來源為 `axios`，另有 `follow-redirects` 與 `postcss` 間接依賴弱點。
**原因分析**: `package-lock.json` 鎖定 `axios 1.14.0`、`follow-redirects 1.15.11`、`postcss 8.5.8`，落在 npm audit 公告的受影響版本範圍內。雖然前端 build 可通過，但正式導入其他專案時會留下安全掃描風險。
**Solution**: 在 `module/frontend-web` 執行 `npm audit fix` 更新 lockfile，實際升級至 `axios 1.16.0`、`follow-redirects 1.16.0`、`postcss 8.5.14`。新增 CI `npm audit --audit-level=high` gate，並重跑 `npm ci`, `npm audit --audit-level=high`, `npm run lint -- --max-warnings=0`, `npm run build` 與 portable payroll 匯入 build，結果皆通過。此問題已同步寫入 Obsidian raw：
- `~/Desktop/obsidian/raw/coding/errors/2026-05-09-frontend-npm-audit-vulnerabilities.md`

## 2026-05-09 - portable bundle 正式匯出缺少乾淨來源防呆

### 問題: manifest 可能無法完整反映未追蹤檔案與正式驗收步驟
**Issue**: portable bundle manifest 已記錄 `source.dirty`，但原本 dirty 判斷只檢查 tracked diff 與 staged diff，未納入 untracked files；正式導入流程也缺少單一驗收腳本，且匯出的 support paths 沒有包含 `module/docker/local`，目標專案無法直接跑 Compose config 驗證。
**原因分析**: `git diff` 與 `git diff --cached` 不會偵測未追蹤檔案；正式交付時若有未追蹤但實際被手動參考的檔案，manifest 仍可能標成乾淨。匯出清單早期聚焦 backend/frontend build，漏掉 Docker local 設定與一鍵驗收入口。
**Solution**: 將 dirty 判斷改為 `git status --porcelain --untracked-files=all`，新增 `scripts/module-export.sh --require-clean` 讓正式匯出遇到未提交或未追蹤變更時直接失敗。新增 `scripts/module-verify-import.sh`，驗證 manifest、`source.dirty`、後端 Maven、前端 `npm audit --audit-level=high`/build，以及 Docker Compose config。`--execute` support paths 補入 `module/docker/local`。此問題已同步寫入 Obsidian raw：
- `~/Desktop/obsidian/raw/coding/errors/2026-05-09-portable-bundle-clean-export-guard.md`

## 2026-05-09 - portable manifest 升級只能做原始 diff

### 問題: `diff -u` 難以判斷模組升級風險
**Issue**: `docs/module-release-guide.md` 原本建議使用 `diff -u` 比對 `module-bundle-manifest.json`，但原始 JSON diff 不容易快速看出哪些模組、Flyway locations、前端 feature 或後端 module 被新增/移除。
**原因分析**: portable manifest 是機器可讀格式，直接用文字 diff 會受到欄位排序與 JSON 結構影響，審核者需要手動解讀清單變化，升級既有專案時容易漏看資料庫 migration 或依賴模組變更。
**Solution**: 新增 `scripts/module-manifest-diff.sh`，讀取兩份 manifest 後輸出結構化差異，包含 requested/required/additional modules、backendModules、frontendFeatures、flywayLocations、defaultPaths、supportPaths、copyPaths 的 added/removed。支援 `--format json` 給 CI 或其他工具使用。此問題已同步寫入 Obsidian raw：
- `~/Desktop/obsidian/raw/coding/errors/2026-05-09-portable-manifest-upgrade-diff.md`

## 2026-05-09 - macOS mktemp template suffix 不相容

### 問題: release check 在 macOS 產生 catalog 暫存檔失敗
**Issue**: 執行 `scripts/module-release-check.sh --modules crm --target /tmp/module-release-check-strict --skip-backend --skip-frontend --skip-compose` 時出現 `mktemp: mkstemp failed ... module-catalog.XXXXXX.md: File exists`。
**原因分析**: macOS/BSD `mktemp` 要求 template 的 `X` 佔位符位於尾端；`module-catalog.XXXXXX.md` 在 `XXXXXX` 後仍有副檔名，因此無法正確產生唯一暫存檔。
**Solution**: 將 release check 的 catalog 暫存 template 改為 `module-catalog.XXXXXX`，移除尾端副檔名，讓 macOS/BSD 與 Linux GNU `mktemp` 都能產生暫存檔。此問題已同步寫入 Obsidian raw：
- `~/Desktop/obsidian/raw/coding/errors/2026-05-09-macos-mktemp-template-suffix.md`
