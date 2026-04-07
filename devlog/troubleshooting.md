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
