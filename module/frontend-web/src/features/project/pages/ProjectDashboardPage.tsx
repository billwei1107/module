/**
 * @file ProjectDashboardPage.tsx
 * @description 專案任務驗證頁 / Project dashboard verification page
 * @description_en Provides a minimal smoke-test UI for project module APIs
 * @description_zh 提供母體庫驗證專案任務 API 的最小頁面
 */

import { useEffect, useState } from 'react';
import {
    Alert,
    Box,
    Button,
    Chip,
    Paper,
    Stack,
    TextField,
    Typography,
} from '@mui/material';
import { createProject, getGanttData, getKanbanBoard, getProjects } from '../api/projectApi';
import type { GanttData, KanbanBoard, Project } from '../types';

export const ProjectDashboardPage = () => {
    const [projects, setProjects] = useState<Project[]>([]);
    const [kanban, setKanban] = useState<KanbanBoard | null>(null);
    const [gantt, setGantt] = useState<GanttData | null>(null);
    const [projectName, setProjectName] = useState('導入專案');
    const [message, setMessage] = useState('');

    const loadData = async () => {
        const projectResult = await getProjects();
        setProjects(projectResult);
        const firstProject = projectResult[0];
        if (firstProject) {
            const [kanbanResult, ganttResult] = await Promise.all([
                getKanbanBoard(firstProject.id),
                getGanttData(firstProject.id),
            ]);
            setKanban(kanbanResult);
            setGantt(ganttResult);
        }
    };

    useEffect(() => {
        loadData().catch(() => setMessage('載入專案資料失敗 / Failed to load project data'));
    }, []);

    const handleCreateProject = async () => {
        try {
            await createProject({
                name: projectName,
                ownerId: 'emp-001',
                startDate: '2026-05-01',
                endDate: '2026-06-30',
                description: '母體庫驗證專案',
            });
            setMessage('專案已建立 / Project created');
            await loadData();
        } catch {
            setMessage('建立專案失敗 / Failed to create project');
        }
    };

    return (
        <Stack spacing={3}>
            <Typography variant="h5">專案任務</Typography>
            {message && <Alert severity="info">{message}</Alert>}

            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>專案清單</Typography>
                <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ mb: 2 }}>
                    <TextField label="專案名稱" value={projectName} onChange={(event) => setProjectName(event.target.value)} />
                    <Button variant="contained" onClick={handleCreateProject}>新增專案</Button>
                </Stack>
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                    {projects.map((project) => (
                        <Chip key={project.id} label={`${project.name}: ${project.status}`} />
                    ))}
                </Box>
            </Paper>

            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>看板</Typography>
                <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
                    <Box sx={{ flex: 1 }}>
                        <Typography fontWeight="bold">TODO</Typography>
                        {kanban?.todo.map((task) => <Typography key={task.id}>{task.title}</Typography>)}
                    </Box>
                    <Box sx={{ flex: 1 }}>
                        <Typography fontWeight="bold">IN PROGRESS</Typography>
                        {kanban?.inProgress.map((task) => <Typography key={task.id}>{task.title}</Typography>)}
                    </Box>
                    <Box sx={{ flex: 1 }}>
                        <Typography fontWeight="bold">DONE</Typography>
                        {kanban?.done.map((task) => <Typography key={task.id}>{task.title}</Typography>)}
                    </Box>
                </Stack>
            </Paper>

            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>甘特圖資料</Typography>
                <Typography>任務數：{gantt?.tasks.length ?? 0}</Typography>
                <Typography>里程碑數：{gantt?.milestones.length ?? 0}</Typography>
            </Paper>
        </Stack>
    );
};
