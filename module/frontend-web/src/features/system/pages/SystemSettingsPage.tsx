/**
 * @file SystemSettingsPage.tsx
 * @description 系統設定驗證頁 / System settings verification page
 * @description_zh 提供母體庫驗證系統設定、功能開關、資料字典與流水號 API 的最小頁面
 */

import { useEffect, useState } from 'react';
import {
    Alert,
    Box,
    Button,
    Chip,
    Divider,
    Paper,
    Stack,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableRow,
    TextField,
    Typography,
} from '@mui/material';
import {
    getDictionaries,
    getCurrentFeatureInstallationPlan,
    getFeatureDependencyIssues,
    getFeatureToggles,
    getNextSequence,
    getSystemConfigs,
    upsertSystemConfig,
} from '../api/systemApi';
import type { Dictionary, FeatureDependencyIssue, FeatureInstallationPlan, FeatureToggle, SystemConfig } from '../types';

export const SystemSettingsPage = () => {
    const [configs, setConfigs] = useState<SystemConfig[]>([]);
    const [features, setFeatures] = useState<FeatureToggle[]>([]);
    const [dependencyIssues, setDependencyIssues] = useState<FeatureDependencyIssue[]>([]);
    const [installationPlan, setInstallationPlan] = useState<FeatureInstallationPlan | null>(null);
    const [dictionaries, setDictionaries] = useState<Dictionary[]>([]);
    const [systemName, setSystemName] = useState('模塊化企業系統');
    const [sequence, setSequence] = useState('');
    const [message, setMessage] = useState('');

    const loadData = async () => {
        const [configResult, featureResult, dependencyIssueResult, installationPlanResult, dictionaryResult] = await Promise.all([
            getSystemConfigs(),
            getFeatureToggles(),
            getFeatureDependencyIssues(),
            getCurrentFeatureInstallationPlan(),
            getDictionaries(),
        ]);
        setConfigs(configResult);
        setFeatures(featureResult);
        setDependencyIssues(dependencyIssueResult);
        setInstallationPlan(installationPlanResult);
        setDictionaries(dictionaryResult);
        const nameConfig = configResult.find((config) => config.key === 'system.name');
        if (nameConfig) {
            setSystemName(nameConfig.value);
        }
    };

    useEffect(() => {
        loadData().catch(() => setMessage('載入系統設定失敗 / Failed to load system settings'));
    }, []);

    // ========================================
    // 儲存設定 / Save Config
    // ========================================
    const handleSaveSystemName = async () => {
        try {
            await upsertSystemConfig({
                key: 'system.name',
                value: systemName,
                category: 'general',
                description: '系統顯示名稱',
            });
            setMessage('系統設定已更新 / System config updated');
            await loadData();
        } catch {
            setMessage('系統設定更新失敗 / Failed to update system config');
        }
    };

    const handleGenerateSequence = async () => {
        try {
            setSequence(await getNextSequence('EMP'));
        } catch {
            setMessage('流水號產生失敗 / Failed to generate sequence');
        }
    };

    return (
        <Stack spacing={3}>
            <Typography variant="h5">系統設定</Typography>
            {message && <Alert severity="info">{message}</Alert>}

            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>全域設定</Typography>
                <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
                    <TextField
                        label="系統名稱"
                        value={systemName}
                        onChange={(event) => setSystemName(event.target.value)}
                        fullWidth
                    />
                    <Button variant="contained" onClick={handleSaveSystemName}>
                        儲存
                    </Button>
                </Stack>
                <Table size="small" sx={{ mt: 2 }}>
                    <TableHead>
                        <TableRow>
                            <TableCell>Key</TableCell>
                            <TableCell>Value</TableCell>
                            <TableCell>Category</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {configs.map((config) => (
                            <TableRow key={config.key}>
                                <TableCell>{config.key}</TableCell>
                                <TableCell>{config.value}</TableCell>
                                <TableCell>{config.category}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </Paper>

            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>功能開關</Typography>
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                    {features.map((feature) => (
                        <Chip
                            key={feature.module}
                            label={`${feature.displayName ?? feature.module} (${feature.priority ?? 'P?'})：${feature.enabled ? 'ON' : 'OFF'}`}
                            color={feature.enabled ? 'success' : 'default'}
                        />
                    ))}
                </Box>
                {dependencyIssues.length > 0 && (
                    <Alert severity="warning" sx={{ mt: 2 }}>
                        {dependencyIssues.map((issue) => (
                            <Typography key={issue.module} variant="body2">
                                {issue.displayName} 缺少依賴：{issue.missingDependencies.join(', ')}
                            </Typography>
                        ))}
                    </Alert>
                )}
                {installationPlan && (
                    <>
                        <Divider sx={{ my: 2 }} />
                        <Stack spacing={1}>
                            <Typography variant="subtitle1">目前啟用模組搬移清單</Typography>
                            <Typography variant="body2" color="text.secondary">
                                必備模組：{installationPlan.requiredModules.join(', ') || '無'}
                            </Typography>
                            {installationPlan.additionalModules.length > 0 && (
                                <Alert severity="info">
                                    需額外導入：{installationPlan.additionalModules.join(', ')}
                                </Alert>
                            )}
                            <Typography variant="body2">
                                後端：{installationPlan.backendModules.join('、') || '無'}
                            </Typography>
                            <Typography variant="body2">
                                前端：{installationPlan.frontendFeatures.join('、') || '無'}
                            </Typography>
                            <Typography variant="body2">
                                Flyway：{installationPlan.flywayLocations.join('、') || '無'}
                            </Typography>
                        </Stack>
                    </>
                )}
            </Paper>

            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>資料字典</Typography>
                <Stack spacing={1}>
                    {dictionaries.map((dictionary) => (
                        <Box key={dictionary.code}>
                            <Typography fontWeight="bold">{dictionary.name} ({dictionary.code})</Typography>
                            <Typography color="text.secondary" variant="body2">
                                {dictionary.items?.length ?? 0} 個項目
                            </Typography>
                        </Box>
                    ))}
                </Stack>
            </Paper>

            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>流水號驗證</Typography>
                <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} alignItems="center">
                    <Button variant="outlined" onClick={handleGenerateSequence}>產生 EMP 流水號</Button>
                    {sequence && <Typography>{sequence}</Typography>}
                </Stack>
            </Paper>
        </Stack>
    );
};
