-- =============================================
-- 系統設定模組資料表 / System Module Tables
-- =============================================

CREATE TABLE sys_configs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT,
    category VARCHAR(50) NOT NULL DEFAULT 'general',
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_sys_configs_category ON sys_configs(category);

CREATE TABLE sys_dictionaries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE sys_dictionary_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    dictionary_id UUID NOT NULL REFERENCES sys_dictionaries(id),
    label VARCHAR(100) NOT NULL,
    value VARCHAR(100) NOT NULL,
    sort_order INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_sys_dictionary_items_dictionary ON sys_dictionary_items(dictionary_id);

CREATE TABLE sys_sequence_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,
    prefix VARCHAR(30) NOT NULL,
    date_format VARCHAR(20) DEFAULT 'yyyy',
    current_value BIGINT NOT NULL DEFAULT 0,
    pad_length INTEGER NOT NULL DEFAULT 4,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

INSERT INTO sys_configs (config_key, config_value, category, description)
VALUES
    ('system.name', '模塊化企業系統', 'general', '系統顯示名稱'),
    ('company.name', 'BillW Enterprise', 'general', '預設公司名稱')
ON CONFLICT (config_key) DO NOTHING;

INSERT INTO sys_dictionaries (code, name)
VALUES
    ('employee_status', '員工狀態'),
    ('approval_status', '審批狀態')
ON CONFLICT (code) DO NOTHING;

INSERT INTO sys_sequence_rules (name, prefix, date_format, current_value, pad_length)
VALUES
    ('EMP', 'EMP', 'yyyy', 0, 4)
ON CONFLICT (name) DO NOTHING;
