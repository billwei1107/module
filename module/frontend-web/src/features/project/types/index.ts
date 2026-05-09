/**
 * @file index.ts
 * @description 專案任務型別定義 / Project type definitions
 * @description_en Defines project module API data structures for smoke verification UI
 * @description_zh 定義專案任務模組驗證頁使用的 API 資料結構
 */

export interface Project {
    id: string;
    name: string;
    ownerId?: string;
    startDate?: string;
    endDate?: string;
    status: string;
    description?: string;
}

export interface Task {
    id: string;
    projectId: string;
    title: string;
    assigneeId?: string;
    dependencyIds: string[];
    status: 'TODO' | 'IN_PROGRESS' | 'DONE';
    startDate?: string;
    dueDate?: string;
}

export interface KanbanBoard {
    todo: Task[];
    inProgress: Task[];
    done: Task[];
}

export interface Milestone {
    id: string;
    projectId: string;
    name: string;
    dueDate?: string;
    completed: boolean;
}

export interface GanttData {
    tasks: Task[];
    milestones: Milestone[];
}
