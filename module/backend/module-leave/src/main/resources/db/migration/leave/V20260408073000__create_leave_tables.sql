-- =============================================
-- 請假管理模組資料表 / Leave Management Module Tables
-- =============================================

CREATE TABLE leave_types (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL,
    code VARCHAR(30) NOT NULL UNIQUE,
    annual_quota_hours INTEGER NOT NULL DEFAULT 0,
    requires_approval BOOLEAN NOT NULL DEFAULT TRUE,
    paid BOOLEAN NOT NULL DEFAULT TRUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE leave_balances (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id VARCHAR(36) NOT NULL,
    leave_type_id UUID NOT NULL REFERENCES leave_types(id),
    year INTEGER NOT NULL,
    total_hours INTEGER NOT NULL DEFAULT 0,
    used_hours INTEGER NOT NULL DEFAULT 0,
    reserved_hours INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE UNIQUE INDEX uk_leave_balances_employee_type_year
    ON leave_balances(employee_id, leave_type_id, year)
    WHERE deleted_at IS NULL;

CREATE TABLE leave_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id VARCHAR(36) NOT NULL,
    leave_type_id UUID NOT NULL REFERENCES leave_types(id),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    hours INTEGER NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    workflow_instance_id VARCHAR(36),
    reviewed_by VARCHAR(36),
    reviewed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_leave_requests_employee ON leave_requests(employee_id);
CREATE INDEX idx_leave_requests_status ON leave_requests(status);

INSERT INTO leave_types (name, code, annual_quota_hours, requires_approval, paid)
VALUES
    ('特休', 'ANNUAL', 112, TRUE, TRUE),
    ('病假', 'SICK', 240, TRUE, FALSE),
    ('事假', 'PERSONAL', 112, TRUE, FALSE)
ON CONFLICT (code) DO NOTHING;
