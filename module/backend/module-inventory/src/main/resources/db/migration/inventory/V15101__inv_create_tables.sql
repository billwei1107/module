-- =============================================
-- 庫存管理模組資料表 / Inventory Module Tables
-- =============================================

CREATE TABLE inv_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(120) NOT NULL,
    code VARCHAR(40),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE inv_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    category_id UUID REFERENCES inv_categories(id),
    sku VARCHAR(80) NOT NULL UNIQUE,
    name VARCHAR(180) NOT NULL,
    specification VARCHAR(120),
    barcode VARCHAR(80),
    unit VARCHAR(20) NOT NULL DEFAULT 'PCS',
    safety_stock NUMERIC(19, 4) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);
CREATE INDEX idx_inv_items_category ON inv_items(category_id);
CREATE INDEX idx_inv_items_barcode ON inv_items(barcode);

CREATE TABLE inv_warehouses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(60) NOT NULL UNIQUE,
    name VARCHAR(160) NOT NULL,
    location VARCHAR(240),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE inv_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    item_id UUID NOT NULL REFERENCES inv_items(id),
    warehouse_id UUID NOT NULL REFERENCES inv_warehouses(id),
    quantity NUMERIC(19, 4) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT uq_inv_records_item_warehouse UNIQUE (item_id, warehouse_id)
);
CREATE INDEX idx_inv_records_item ON inv_records(item_id);
CREATE INDEX idx_inv_records_warehouse ON inv_records(warehouse_id);

CREATE TABLE inv_movements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    item_id UUID NOT NULL REFERENCES inv_items(id),
    from_warehouse_id UUID REFERENCES inv_warehouses(id),
    to_warehouse_id UUID REFERENCES inv_warehouses(id),
    type VARCHAR(20) NOT NULL,
    quantity NUMERIC(19, 4) NOT NULL DEFAULT 0,
    reference_no VARCHAR(80),
    note TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);
CREATE INDEX idx_inv_movements_item ON inv_movements(item_id);
CREATE INDEX idx_inv_movements_type ON inv_movements(type);

CREATE TABLE inv_stock_takes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    item_id UUID NOT NULL REFERENCES inv_items(id),
    warehouse_id UUID NOT NULL REFERENCES inv_warehouses(id),
    expected_quantity NUMERIC(19, 4) NOT NULL DEFAULT 0,
    actual_quantity NUMERIC(19, 4),
    difference_quantity NUMERIC(19, 4),
    status VARCHAR(20) NOT NULL DEFAULT 'FROZEN',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);
CREATE INDEX idx_inv_stock_takes_item ON inv_stock_takes(item_id);
CREATE INDEX idx_inv_stock_takes_warehouse ON inv_stock_takes(warehouse_id);
