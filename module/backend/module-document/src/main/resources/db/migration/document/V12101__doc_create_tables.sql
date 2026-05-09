-- =============================================
-- 文件管理模組資料表 / Document Module Tables
-- =============================================

CREATE TABLE doc_folders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    parent_id UUID REFERENCES doc_folders(id),
    name VARCHAR(160) NOT NULL,
    path VARCHAR(500) NOT NULL,
    owner_id VARCHAR(80),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_doc_folders_parent ON doc_folders(parent_id);
CREATE INDEX idx_doc_folders_owner ON doc_folders(owner_id);

CREATE TABLE doc_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    folder_id UUID REFERENCES doc_folders(id),
    file_name VARCHAR(220) NOT NULL,
    file_path VARCHAR(800) NOT NULL,
    mime_type VARCHAR(120) NOT NULL,
    file_size BIGINT NOT NULL DEFAULT 0,
    version INTEGER NOT NULL DEFAULT 1,
    owner_id VARCHAR(80),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_doc_documents_folder ON doc_documents(folder_id);
CREATE INDEX idx_doc_documents_owner ON doc_documents(owner_id);
CREATE INDEX idx_doc_documents_file_name ON doc_documents(file_name);

CREATE TABLE doc_versions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_id UUID NOT NULL REFERENCES doc_documents(id),
    version INTEGER NOT NULL,
    file_path VARCHAR(800) NOT NULL,
    mime_type VARCHAR(120) NOT NULL,
    file_size BIGINT NOT NULL DEFAULT 0,
    uploaded_by VARCHAR(80),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT uq_doc_versions_document_version UNIQUE (document_id, version)
);

CREATE INDEX idx_doc_versions_document ON doc_versions(document_id);

CREATE TABLE doc_shares (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_id UUID NOT NULL REFERENCES doc_documents(id),
    shared_with VARCHAR(80) NOT NULL,
    permission VARCHAR(20) NOT NULL DEFAULT 'READ',
    shared_by VARCHAR(80),
    expires_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_doc_shares_document ON doc_shares(document_id);
CREATE INDEX idx_doc_shares_shared_with ON doc_shares(shared_with);

CREATE TABLE doc_tags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_id UUID NOT NULL REFERENCES doc_documents(id),
    name VARCHAR(80) NOT NULL,
    color VARCHAR(24) NOT NULL DEFAULT '#64748b',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_doc_tags_document ON doc_tags(document_id);
CREATE INDEX idx_doc_tags_name ON doc_tags(name);
