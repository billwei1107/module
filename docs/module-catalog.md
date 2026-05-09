# 模組清冊 / Module Catalog

本文件由 `module/backend/module-system/src/main/resources/module-catalog.tsv` 生成，用於確認可移植模組的依賴、路由與來源位置。

| Module | Name | Phase | Priority | Dependencies | Backend | Frontend | Flyway | Default route |
|---|---|---|---|---|---|---|---|---|
| `auth` | 認證授權 / Authentication | `CORE` | `P0` | - | `module/backend/module-auth` | `module/frontend-web/src/features/auth` | `classpath:db/migration` | `/login` |
| `organization` | 組織管理 / Organization | `CORE` | `P0` | auth | `module/backend/module-organization` | `module/frontend-web/src/features/organization` | `classpath:db/migration/organization` | `/department` |
| `workflow` | 審批流程 / Workflow | `CORE` | `P0` | auth, organization | `module/backend/module-workflow` | `module/frontend-web/src/features/workflow` | `classpath:db/migration/workflow` | `/workflow` |
| `notification` | 通知中心 / Notification | `OPERATIONS` | `P1` | auth, organization | `module/backend/module-notification` | `module/frontend-web/src/features/notification` | `classpath:db/migration/notification` | `-` |
| `attendance` | 打卡考勤 / Attendance | `OPERATIONS` | `P1` | auth, organization | `module/backend/module-attendance` | `module/frontend-web/src/features/attendance` | `classpath:db/migration/attendance` | `/attendance/clock-in` |
| `leave` | 請假管理 / Leave Management | `OPERATIONS` | `P1` | auth, organization, workflow, attendance | `module/backend/module-leave` | `module/frontend-web/src/features/leave` | `classpath:db/migration/leave` | `/leave/requests` |
| `system` | 系統設定 / System Settings | `OPERATIONS` | `P1` | auth | `module/backend/module-system` | `module/frontend-web/src/features/system` | `classpath:db/migration/system` | `/system` |
| `audit` | 稽核日誌 / Audit Log | `OPERATIONS` | `P1` | auth | `module/backend/module-audit` | `module/frontend-web/src/features/audit` | `classpath:db/migration/audit` | `/audit/logs` |
| `finance` | 財務管理 / Finance | `OPERATIONS` | `P1` | auth, organization, workflow | `module/backend/module-finance` | `module/frontend-web/src/features/finance` | `classpath:db/migration/finance` | `/finance` |
| `payroll` | 薪資管理 / Payroll | `EXTENSION` | `P2` | auth, organization, attendance, leave, finance | `module/backend/module-payroll` | `module/frontend-web/src/features/payroll` | `classpath:db/migration/payroll` | `/payroll` |
| `project` | 專案任務 / Project Management | `EXTENSION` | `P2` | auth, organization, notification | `module/backend/module-project` | `module/frontend-web/src/features/project` | `classpath:db/migration/project` | `/projects` |
| `document` | 文件管理 / Document Management | `EXTENSION` | `P2` | auth, organization | `module/backend/module-document` | `module/frontend-web/src/features/document` | `classpath:db/migration/document` | `/documents` |
| `report` | 報表分析 / Report Analytics | `EXTENSION` | `P2` | auth, organization | `module/backend/module-report` | `module/frontend-web/src/features/report` | `classpath:db/migration/report` | `/reports` |
| `crm` | 客戶管理 / CRM | `EXTENSION` | `P2` | auth, organization | `module/backend/module-crm` | `module/frontend-web/src/features/crm` | `classpath:db/migration/crm` | `/crm` |
| `inventory` | 庫存管理 / Inventory | `ADVANCED` | `P3` | auth, organization | `module/backend/module-inventory` | `module/frontend-web/src/features/inventory` | `classpath:db/migration/inventory` | `/inventory` |
| `meeting` | 會議管理 / Meeting Management | `ADVANCED` | `P3` | auth, organization, notification | `module/backend/module-meeting` | `module/frontend-web/src/features/meeting` | `classpath:db/migration/meeting` | `/meetings` |
| `announcement` | 公告系統 / Announcement | `ADVANCED` | `P3` | auth, organization, notification | `module/backend/module-announcement` | `module/frontend-web/src/features/announcement` | `classpath:db/migration/announcement` | `/announcements` |
