-- =============================================
-- 薪資管理模組資料表 / Payroll Module Tables
-- =============================================

CREATE TABLE pay_salary_structures (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(120) NOT NULL,
    employee_id VARCHAR(80) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL DEFAULT 'MONTHLY',
    base_salary DECIMAL(19,4) NOT NULL DEFAULT 0,
    hourly_rate DECIMAL(19,4) NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_pay_salary_structures_employee ON pay_salary_structures(employee_id);

CREATE TABLE pay_salary_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(120) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    category VARCHAR(20) NOT NULL,
    calculation_type VARCHAR(20) NOT NULL DEFAULT 'FIXED',
    amount DECIMAL(19,4) NOT NULL DEFAULT 0,
    percentage DECIMAL(9,6) NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE pay_payroll_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id VARCHAR(80) NOT NULL,
    year_month VARCHAR(7) NOT NULL,
    base_salary DECIMAL(19,4) NOT NULL DEFAULT 0,
    total_earnings DECIMAL(19,4) NOT NULL DEFAULT 0,
    total_deductions DECIMAL(19,4) NOT NULL DEFAULT 0,
    net_pay DECIMAL(19,4) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    confirmed_by VARCHAR(80),
    paid_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT uk_payroll_employee_month UNIQUE (employee_id, year_month)
);

CREATE INDEX idx_pay_payroll_records_month ON pay_payroll_records(year_month);
CREATE INDEX idx_pay_payroll_records_employee ON pay_payroll_records(employee_id);

CREATE TABLE pay_payroll_details (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payroll_record_id UUID NOT NULL REFERENCES pay_payroll_records(id),
    salary_item_id UUID,
    item_code VARCHAR(50) NOT NULL,
    item_name VARCHAR(120) NOT NULL,
    category VARCHAR(20) NOT NULL,
    amount DECIMAL(19,4) NOT NULL DEFAULT 0,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_pay_payroll_details_record ON pay_payroll_details(payroll_record_id);

CREATE TABLE pay_tax_configs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bracket_start DECIMAL(19,4) NOT NULL DEFAULT 0,
    bracket_end DECIMAL(19,4),
    rate DECIMAL(9,6) NOT NULL DEFAULT 0,
    deduction DECIMAL(19,4) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE pay_insurance_configs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type VARCHAR(20) NOT NULL UNIQUE,
    employee_rate DECIMAL(9,6) NOT NULL DEFAULT 0,
    employer_rate DECIMAL(9,6) NOT NULL DEFAULT 0,
    ceiling DECIMAL(19,4) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE pay_payroll_adjustments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id VARCHAR(80) NOT NULL,
    year_month VARCHAR(7) NOT NULL,
    adjustment_type VARCHAR(40) NOT NULL,
    amount DECIMAL(19,4) NOT NULL DEFAULT 0,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_pay_adjustments_employee_month ON pay_payroll_adjustments(employee_id, year_month);

INSERT INTO pay_tax_configs (bracket_start, bracket_end, rate, deduction)
VALUES
    (0, 50000, 0.050000, 0),
    (50000, 100000, 0.120000, 3500),
    (100000, NULL, 0.200000, 11500);

INSERT INTO pay_insurance_configs (type, employee_rate, employer_rate, ceiling)
VALUES
    ('LABOR', 0.020000, 0.070000, 45800),
    ('HEALTH', 0.015000, 0.050000, 200000);
