-- =============================================
-- 報表分析模組資料表 / Report Module Tables
-- =============================================

CREATE TABLE rpt_definitions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(160) NOT NULL,
    data_source_sql TEXT NOT NULL,
    columns_json TEXT NOT NULL DEFAULT '[]',
    filters_json TEXT NOT NULL DEFAULT '{}',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_rpt_definitions_active ON rpt_definitions(active);

CREATE TABLE rpt_schedules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    definition_id UUID NOT NULL REFERENCES rpt_definitions(id),
    cron_expression VARCHAR(120) NOT NULL,
    recipient_emails TEXT NOT NULL DEFAULT '',
    last_run_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_rpt_schedules_definition ON rpt_schedules(definition_id);
CREATE INDEX idx_rpt_schedules_active ON rpt_schedules(active);

CREATE TABLE rpt_dashboard_configs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(160) NOT NULL,
    owner_id VARCHAR(80),
    layout_json TEXT NOT NULL DEFAULT '{}',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_rpt_dashboard_configs_owner ON rpt_dashboard_configs(owner_id);

CREATE TABLE rpt_widgets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    dashboard_id UUID NOT NULL REFERENCES rpt_dashboard_configs(id),
    title VARCHAR(160) NOT NULL,
    type VARCHAR(20) NOT NULL DEFAULT 'NUMBER',
    data_source_sql TEXT NOT NULL,
    position_json TEXT NOT NULL DEFAULT '{}',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_rpt_widgets_dashboard ON rpt_widgets(dashboard_id);
