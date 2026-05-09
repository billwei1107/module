-- =============================================
-- 專案任務模組資料表 / Project Module Tables
-- =============================================

CREATE TABLE proj_projects (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(160) NOT NULL,
    owner_id VARCHAR(80),
    start_date DATE,
    end_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'PLANNING',
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE proj_members (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES proj_projects(id),
    employee_id VARCHAR(80) NOT NULL,
    role VARCHAR(60) NOT NULL DEFAULT 'MEMBER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_proj_members_project ON proj_members(project_id);
CREATE INDEX idx_proj_members_employee ON proj_members(employee_id);

CREATE TABLE proj_tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES proj_projects(id),
    title VARCHAR(160) NOT NULL,
    description TEXT,
    assignee_id VARCHAR(80),
    parent_id UUID,
    dependency_ids TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'TODO',
    start_date DATE,
    due_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_proj_tasks_project ON proj_tasks(project_id);
CREATE INDEX idx_proj_tasks_status ON proj_tasks(status);
CREATE INDEX idx_proj_tasks_due_date ON proj_tasks(due_date);

CREATE TABLE proj_task_comments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id UUID NOT NULL REFERENCES proj_tasks(id),
    author_id VARCHAR(80) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_proj_task_comments_task ON proj_task_comments(task_id);

CREATE TABLE proj_task_attachments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id UUID NOT NULL REFERENCES proj_tasks(id),
    file_name VARCHAR(180) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    uploaded_by VARCHAR(80),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_proj_task_attachments_task ON proj_task_attachments(task_id);

CREATE TABLE proj_milestones (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES proj_projects(id),
    name VARCHAR(160) NOT NULL,
    due_date DATE,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_proj_milestones_project ON proj_milestones(project_id);

CREATE TABLE proj_time_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id UUID NOT NULL REFERENCES proj_tasks(id),
    employee_id VARCHAR(80) NOT NULL,
    started_at TIMESTAMP,
    ended_at TIMESTAMP,
    minutes INTEGER NOT NULL DEFAULT 0,
    note TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_proj_time_logs_task ON proj_time_logs(task_id);
CREATE INDEX idx_proj_time_logs_employee ON proj_time_logs(employee_id);
