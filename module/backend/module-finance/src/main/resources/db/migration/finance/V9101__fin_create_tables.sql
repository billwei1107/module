-- =============================================
-- 財務管理模組資料表 / Finance Module Tables
-- =============================================

CREATE TABLE fin_accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    parent_id UUID,
    type VARCHAR(20) NOT NULL,
    level INTEGER NOT NULL DEFAULT 1,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_fin_accounts_parent ON fin_accounts(parent_id);
CREATE INDEX idx_fin_accounts_type ON fin_accounts(type);

CREATE TABLE fin_journal_entries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entry_no VARCHAR(50) NOT NULL UNIQUE,
    entry_date DATE NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    total_debit DECIMAL(19,4) NOT NULL DEFAULT 0,
    total_credit DECIMAL(19,4) NOT NULL DEFAULT 0,
    posted_by VARCHAR(80),
    posted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_fin_journal_entries_date ON fin_journal_entries(entry_date);
CREATE INDEX idx_fin_journal_entries_status ON fin_journal_entries(status);

CREATE TABLE fin_journal_lines (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    journal_entry_id UUID NOT NULL REFERENCES fin_journal_entries(id),
    account_id UUID NOT NULL REFERENCES fin_accounts(id),
    debit_amount DECIMAL(19,4) NOT NULL DEFAULT 0,
    credit_amount DECIMAL(19,4) NOT NULL DEFAULT 0,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_fin_journal_lines_entry ON fin_journal_lines(journal_entry_id);
CREATE INDEX idx_fin_journal_lines_account ON fin_journal_lines(account_id);

CREATE TABLE fin_invoices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_no VARCHAR(50) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL,
    counterparty_id VARCHAR(80),
    amount DECIMAL(19,4) NOT NULL DEFAULT 0,
    paid_amount DECIMAL(19,4) NOT NULL DEFAULT 0,
    due_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_fin_invoices_due_date ON fin_invoices(due_date);
CREATE INDEX idx_fin_invoices_status ON fin_invoices(status);

CREATE TABLE fin_payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id UUID NOT NULL REFERENCES fin_invoices(id),
    amount DECIMAL(19,4) NOT NULL DEFAULT 0,
    payment_date DATE NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    reference_no VARCHAR(80),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_fin_payments_invoice ON fin_payments(invoice_id);

CREATE TABLE fin_expense_claims (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id VARCHAR(80) NOT NULL,
    amount DECIMAL(19,4) NOT NULL DEFAULT 0,
    category VARCHAR(80) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    workflow_instance_id VARCHAR(80),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_fin_expense_claims_employee ON fin_expense_claims(employee_id);
CREATE INDEX idx_fin_expense_claims_status ON fin_expense_claims(status);

CREATE TABLE fin_budgets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(120) NOT NULL,
    department_id VARCHAR(80),
    fiscal_year INTEGER NOT NULL,
    total_amount DECIMAL(19,4) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_fin_budgets_department ON fin_budgets(department_id);
CREATE INDEX idx_fin_budgets_year ON fin_budgets(fiscal_year);

CREATE TABLE fin_budget_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id UUID NOT NULL REFERENCES fin_budgets(id),
    account_id UUID NOT NULL REFERENCES fin_accounts(id),
    planned_amount DECIMAL(19,4) NOT NULL DEFAULT 0,
    actual_amount DECIMAL(19,4) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_fin_budget_items_budget ON fin_budget_items(budget_id);
CREATE INDEX idx_fin_budget_items_account ON fin_budget_items(account_id);
