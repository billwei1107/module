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
