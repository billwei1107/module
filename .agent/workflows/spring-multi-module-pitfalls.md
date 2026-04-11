---
description: Spring Boot 多模組架構常見地雷與除錯指南 (Multi-Module Pitfalls)
---

# Spring Boot 多模組架構常見地雷與排除指南

在開發企業級 Spring Boot 多模組 (Multi-Module) 專案時，經常會遇到模組互相影響或是容器部署不一的啟動地雷。未來在建立新模組或進行重構整合時，請務必嚴格核對此清單，確保系統能平安啟動。

## 1. Flyway 版本號衝突 (Flyway Version Collision)
*   **問題描述**：各子模組各自獨立撰寫時，其 `db/migration` 下的初始腳本可能都習慣性命名為 `V1__init.sql`。然而當整個 Spring Boot 專案啟動時，若掃描到多個不同內容卻帶有相同版號 (`V1`) 的腳本，Flyway 將會拋出 `FlywayException: Found more than one migration with version 1` 並拒絕啟動。
*   **防範手段**：
    *   **模組版號區間化**：明確律定各個模組的首號 (例如 `module-auth` 為 `V1.x`, `module-organization` 為 `V2.x`)。
    *   **聯調整併**：在合併子模組前，務必盤點 `src/main/resources/db/migration/` 中的所有 `.sql` 版本後綴，例如調整為 `V1.1__`, `V1.2__` 並加上特有名稱以避免覆蓋。

## 2. Spring JPA 孤兒地雷 (Strict Repository Scan Override)
*   **問題描述**：在匯入 `Redis` 或其他 Data 模塊後，Spring Data 將會進入嚴格比對模式(Strict Repository Configuration Mode)。如果在此種跨 JAR 模組架構中，有**任何一個**子模組使用了 `@EnableJpaRepositories` (例如 `WorkflowModuleConfig`)，Spring Boot 將會自作聰明地**關閉它預設的全域 Repository 掃描**。這會造成其他仰賴預設掃描的模組 (如 `module-auth`) 因此找不到自己的 JPA 實例而拋棄崩潰：`NoSuchBeanDefinitionException: RoleRepository`。
*   **防範手段**：
    *   **全面去耦配置**：**嚴禁**在主程式 `Application.java` 中宣示任何 JPA 全域掃描路徑。
    *   **模組自帶配置檔**：每個有自己 Repository 的模組，**必須寫死一份自己專屬的 `ModuleConfig.java`**（例如 `AuthModuleConfig`、`OrganizationModuleConfig`）。同時在裡面精準標記自己包路徑的 `@EntityScan` 與 `@EnableJpaRepositories`，讓模組真正能夠即拔即用。

## 3. Bean 註冊遺漏 (Missing Dependency due to Mock Testing)
*   **問題描述**：在撰寫 `AuthServiceImpl` 時，可能呼叫了 `PasswordEncoder` 進行加密。而在單元測試中因為頻繁使用 `@InjectMocks` 或 Mockito 避開了真實環境，這個潛在的「未註冊 Bean」問題會在編譯階段被隱藏，直到整包 Docker 或 Context 啟動時才被無情引爆 (`Parameter X of constructor required a bean that could not be found`)。
*   **防範手段**：
    *   模組設定檔內務必透過 `@Bean` 手動掛載共用元件（如 `BCryptPasswordEncoder`）。
    *   定期使用具備完整 Context 的 `@SpringBootTest` 進行微型整合測試。

## 4. Docker 內外通訊埠錯位 (Container Network Resolving)
*   **問題描述**：若開發手冊內的 `.env` 為方便本機直接連線，把 `POSTGRES_PORT` 設為了 `15432`。當後端的 `application.yml` 盲目吃到這個參數，他會在 Docker 虛擬網路裡試圖尋找 `postgres:15432`。但事實上 Postgres 在容器內網永遠只監聽預設的 `5432` 埠！這將導致後端拋出 `Connection Refused` 假霸工。
*   **防範手段**：
    *   在 `docker-compose.yml` 的 `backend` 服務裡，**強制覆寫**這些環境變數 (例如加上 `environment: - POSTGRES_PORT=5432`)，阻絕外部 `.env` 對容器內網造成的時空錯亂。
    *   不可使用 `localhost` 進行跨容器尋址，必須使用對應的 Service Name (例如 `postgres`, `redis`)。
