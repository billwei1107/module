-- =============================================
-- 客戶管理模組資料表 / CRM Module Tables
-- =============================================

CREATE TABLE crm_customers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(180) NOT NULL,
    type VARCHAR(20) NOT NULL DEFAULT 'COMPANY',
    grade VARCHAR(20) NOT NULL DEFAULT 'PROSPECT',
    owner_id VARCHAR(80),
    phone VARCHAR(40),
    email VARCHAR(160),
    address TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);
CREATE INDEX idx_crm_customers_owner ON crm_customers(owner_id);
CREATE INDEX idx_crm_customers_grade ON crm_customers(grade);

CREATE TABLE crm_contacts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL REFERENCES crm_customers(id),
    name VARCHAR(120) NOT NULL,
    title VARCHAR(120),
    phone VARCHAR(40),
    email VARCHAR(160),
    primary_contact BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);
CREATE INDEX idx_crm_contacts_customer ON crm_contacts(customer_id);

CREATE TABLE crm_opportunities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL REFERENCES crm_customers(id),
    name VARCHAR(180) NOT NULL,
    stage VARCHAR(30) NOT NULL DEFAULT 'LEAD',
    amount NUMERIC(19, 4) NOT NULL DEFAULT 0,
    expected_close_date DATE,
    owner_id VARCHAR(80),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);
CREATE INDEX idx_crm_opportunities_customer ON crm_opportunities(customer_id);
CREATE INDEX idx_crm_opportunities_stage ON crm_opportunities(stage);

CREATE TABLE crm_quotations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quotation_no VARCHAR(60) NOT NULL UNIQUE,
    customer_id UUID NOT NULL REFERENCES crm_customers(id),
    opportunity_id UUID REFERENCES crm_opportunities(id),
    quote_date DATE NOT NULL,
    tax_inclusive BOOLEAN NOT NULL DEFAULT FALSE,
    tax_rate NUMERIC(8, 4) NOT NULL DEFAULT 0,
    subtotal NUMERIC(19, 4) NOT NULL DEFAULT 0,
    tax_amount NUMERIC(19, 4) NOT NULL DEFAULT 0,
    total_amount NUMERIC(19, 4) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);
CREATE INDEX idx_crm_quotations_customer ON crm_quotations(customer_id);

CREATE TABLE crm_quotation_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quotation_id UUID NOT NULL REFERENCES crm_quotations(id),
    item_name VARCHAR(180) NOT NULL,
    quantity NUMERIC(19, 4) NOT NULL DEFAULT 0,
    unit_price NUMERIC(19, 4) NOT NULL DEFAULT 0,
    amount NUMERIC(19, 4) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);
CREATE INDEX idx_crm_quotation_items_quotation ON crm_quotation_items(quotation_id);

CREATE TABLE crm_contracts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    contract_no VARCHAR(60) NOT NULL UNIQUE,
    customer_id UUID NOT NULL REFERENCES crm_customers(id),
    title VARCHAR(180),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    amount NUMERIC(19, 4) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);
CREATE INDEX idx_crm_contracts_customer ON crm_contracts(customer_id);
CREATE INDEX idx_crm_contracts_end_date ON crm_contracts(end_date);

CREATE TABLE crm_interaction_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL REFERENCES crm_customers(id),
    contact_id UUID REFERENCES crm_contacts(id),
    type VARCHAR(20) NOT NULL DEFAULT 'FOLLOW_UP',
    handled_by VARCHAR(80),
    note TEXT NOT NULL,
    next_follow_up_at TIMESTAMP,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);
CREATE INDEX idx_crm_interaction_logs_customer ON crm_interaction_logs(customer_id);
CREATE INDEX idx_crm_interaction_logs_follow_up ON crm_interaction_logs(next_follow_up_at, completed);
