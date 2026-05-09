/**
 * @file moduleNavigation.ts
 * @description 模組導覽與 Feature Toggle 常數 / Module navigation and feature toggle constants
 * @description_en Defines reusable module navigation metadata and enabled-module helpers
 * @description_zh 定義可重用的模組導覽資料與啟用模組判斷工具
 */

export type ModuleKey =
  | 'auth'
  | 'organization'
  | 'workflow'
  | 'notification'
  | 'attendance'
  | 'leave'
  | 'system'
  | 'audit'
  | 'finance'
  | 'payroll'
  | 'project'
  | 'document'
  | 'report'
  | 'crm'
  | 'inventory'
  | 'meeting'
  | 'announcement';

export type EnabledModules = Record<ModuleKey, boolean>;

export interface FeatureToggleLike {
  module: string;
  enabled: boolean;
}

export interface NavigationItem {
  module: ModuleKey;
  label: string;
  path: string;
}

// ========================================
// 預設模組開關 / Default Module Toggles
// ========================================
export const DEFAULT_ENABLED_MODULES: EnabledModules = {
  auth: true,
  organization: true,
  workflow: true,
  notification: true,
  attendance: true,
  leave: true,
  system: true,
  audit: true,
  finance: true,
  payroll: true,
  project: true,
  document: true,
  report: true,
  crm: true,
  inventory: true,
  meeting: true,
  announcement: true,
};

// ========================================
// 導覽項目 / Navigation Items
// ========================================
export const NAVIGATION_ITEMS: NavigationItem[] = [
  { module: 'organization', label: '組織管理', path: '/department' },
  { module: 'organization', label: '員工管理', path: '/employee' },
  { module: 'workflow', label: '發起簽核', path: '/workflow' },
  { module: 'workflow', label: '我的待辦', path: '/my-tasks' },
  { module: 'attendance', label: '打卡', path: '/attendance/clock-in' },
  { module: 'attendance', label: '出勤記錄', path: '/attendance/records' },
  { module: 'leave', label: '請假', path: '/leave/requests' },
  { module: 'leave', label: '請假審核', path: '/leave/approval' },
  { module: 'system', label: '系統設定', path: '/system' },
  { module: 'audit', label: '稽核日誌', path: '/audit/logs' },
  { module: 'finance', label: '財務', path: '/finance' },
  { module: 'payroll', label: '薪資', path: '/payroll' },
  { module: 'project', label: '專案', path: '/projects' },
  { module: 'document', label: '文件', path: '/documents' },
  { module: 'report', label: '報表', path: '/reports' },
  { module: 'crm', label: '客戶', path: '/crm' },
  { module: 'inventory', label: '庫存', path: '/inventory' },
  { module: 'meeting', label: '會議', path: '/meetings' },
  { module: 'announcement', label: '公告', path: '/announcements' },
];

// ========================================
// 模組啟用判斷 / Module Enabled Helpers
// ========================================
export const isModuleEnabled = (enabledModules: EnabledModules, module: ModuleKey) => enabledModules[module];

export const getDefaultPath = (enabledModules: EnabledModules) =>
  NAVIGATION_ITEMS.find((item) => isModuleEnabled(enabledModules, item.module))?.path ?? '/login';

export const toEnabledModules = (features: FeatureToggleLike[]): EnabledModules => {
  const enabledModules = { ...DEFAULT_ENABLED_MODULES };
  features.forEach((feature) => {
    if (feature.module in enabledModules) {
      enabledModules[feature.module as ModuleKey] = feature.enabled;
    }
  });
  return enabledModules;
};
