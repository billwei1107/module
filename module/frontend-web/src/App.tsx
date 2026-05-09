/**
 * @file App.tsx
 * @description 前端路由入口 / Frontend route entry
 * @description_en Wires application routes and module feature-toggle navigation
 * @description_zh 組裝前端路由，並依後端模組開關控制導覽與直連路由
 */

import { useEffect, useMemo, useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, Link } from 'react-router-dom';
import { Box, AppBar, Toolbar, Typography, Button, Container, CssBaseline } from '@mui/material';

// 引入 auth 頁面
import { LoginPage } from './features/auth/pages/LoginPage';
import { RoleListPage } from './features/auth/pages/RoleListPage';

// 引入 organization 頁面
import { CompanyPage } from './features/organization/pages/CompanyPage';
import { DepartmentPage } from './features/organization/pages/DepartmentPage';
import { PositionPage } from './features/organization/pages/PositionPage';
import { EmployeeListPage } from './features/organization/pages/EmployeeListPage';

// 引入 workflow 頁面
import { DefinitionListPage } from './features/workflow/pages/DefinitionListPage';
import { MyTasksPage } from './features/workflow/pages/MyTasksPage';
import { NotificationBell } from './features/notification/components/NotificationBell';

// 引入 attendance 頁面
import { ClockInPage } from './features/attendance/pages/ClockInPage';
import { AttendanceRecordsPage } from './features/attendance/pages/AttendanceRecordsPage';
import { ShiftManagementPage } from './features/attendance/pages/ShiftManagementPage';
import { AttendanceReportPage } from './features/attendance/pages/AttendanceReportPage';
import { LeaveRequestPage } from './features/leave/pages/LeaveRequestPage';
import { LeaveApprovalPage } from './features/leave/pages/LeaveApprovalPage';
import { SystemSettingsPage } from './features/system/pages/SystemSettingsPage';
import { AuditLogPage } from './features/audit/pages/AuditLogPage';
import { FinanceDashboardPage } from './features/finance/pages/FinanceDashboardPage';
import { PayrollDashboardPage } from './features/payroll/pages/PayrollDashboardPage';
import { ProjectDashboardPage } from './features/project/pages/ProjectDashboardPage';
import { DocumentDashboardPage } from './features/document/pages/DocumentDashboardPage';
import { ReportDashboardPage } from './features/report/pages/ReportDashboardPage';
import { CrmDashboardPage } from './features/crm/pages/CrmDashboardPage';
import { InventoryDashboardPage } from './features/inventory/pages/InventoryDashboardPage';
import { MeetingDashboardPage } from './features/meeting/pages/MeetingDashboardPage';
import { AnnouncementDashboardPage } from './features/announcement/pages/AnnouncementDashboardPage';
import { getFeatureToggles } from './features/system/api/systemApi';
import {
  DEFAULT_ENABLED_MODULES,
  getDefaultPath,
  isModuleEnabled,
  NAVIGATION_ITEMS,
  toEnabledModules,
  type EnabledModules,
  type ModuleKey,
} from './shared/navigation/moduleNavigation';

const AppLayout = ({ children, enabledModules }: { children: React.ReactNode; enabledModules: EnabledModules }) => (
  <Box sx={{ flexGrow: 1, minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          模塊化企業系統
        </Typography>
        {enabledModules.notification && <NotificationBell />}
        {NAVIGATION_ITEMS.filter((item) => isModuleEnabled(enabledModules, item.module)).map((item) => (
          <Button key={`${item.module}-${item.path}`} color="inherit" component={Link} to={item.path}>
            {item.label}
          </Button>
        ))}
        <Button color="inherit" component={Link} to="/login">登出</Button>
      </Toolbar>
    </AppBar>
    <Container sx={{ mt: 4, flexGrow: 1 }}>
      {children}
    </Container>
  </Box>
);

const FeatureRoute = ({
  module,
  enabledModules,
  children,
}: {
  module: ModuleKey;
  enabledModules: EnabledModules;
  children: React.ReactNode;
}) => {
  if (!isModuleEnabled(enabledModules, module)) {
    return <Navigate to={getDefaultPath(enabledModules)} replace />;
  }

  return <AppLayout enabledModules={enabledModules}>{children}</AppLayout>;
};

function App() {
  const [enabledModules, setEnabledModules] = useState<EnabledModules>(DEFAULT_ENABLED_MODULES);

  useEffect(() => {
    getFeatureToggles()
      .then((features) => setEnabledModules(toEnabledModules(features)))
      .catch(() => setEnabledModules(DEFAULT_ENABLED_MODULES));
  }, []);

  const defaultPath = useMemo(() => getDefaultPath(enabledModules), [enabledModules]);

  return (
    <>
      <CssBaseline />
      <Router>
        <Routes>
          <Route path="/login" element={<LoginPage />} />

          {/* 受保護的後台區域 */}
          <Route path="/role" element={<FeatureRoute module="auth" enabledModules={enabledModules}><RoleListPage /></FeatureRoute>} />
          <Route path="/company" element={<FeatureRoute module="organization" enabledModules={enabledModules}><CompanyPage /></FeatureRoute>} />
          <Route path="/department" element={<FeatureRoute module="organization" enabledModules={enabledModules}><DepartmentPage /></FeatureRoute>} />
          <Route path="/position" element={<FeatureRoute module="organization" enabledModules={enabledModules}><PositionPage /></FeatureRoute>} />
          <Route path="/employee" element={<FeatureRoute module="organization" enabledModules={enabledModules}><EmployeeListPage /></FeatureRoute>} />
          <Route path="/workflow" element={<FeatureRoute module="workflow" enabledModules={enabledModules}><DefinitionListPage /></FeatureRoute>} />
          <Route path="/my-tasks" element={<FeatureRoute module="workflow" enabledModules={enabledModules}><MyTasksPage /></FeatureRoute>} />
          <Route path="/attendance/clock-in" element={<FeatureRoute module="attendance" enabledModules={enabledModules}><ClockInPage /></FeatureRoute>} />
          <Route path="/attendance/records" element={<FeatureRoute module="attendance" enabledModules={enabledModules}><AttendanceRecordsPage /></FeatureRoute>} />
          <Route path="/attendance/shifts" element={<FeatureRoute module="attendance" enabledModules={enabledModules}><ShiftManagementPage /></FeatureRoute>} />
          <Route path="/attendance/report" element={<FeatureRoute module="attendance" enabledModules={enabledModules}><AttendanceReportPage /></FeatureRoute>} />
          <Route path="/leave/requests" element={<FeatureRoute module="leave" enabledModules={enabledModules}><LeaveRequestPage /></FeatureRoute>} />
          <Route path="/leave/approval" element={<FeatureRoute module="leave" enabledModules={enabledModules}><LeaveApprovalPage /></FeatureRoute>} />
          <Route path="/system" element={<FeatureRoute module="system" enabledModules={enabledModules}><SystemSettingsPage /></FeatureRoute>} />
          <Route path="/audit/logs" element={<FeatureRoute module="audit" enabledModules={enabledModules}><AuditLogPage /></FeatureRoute>} />
          <Route path="/finance" element={<FeatureRoute module="finance" enabledModules={enabledModules}><FinanceDashboardPage /></FeatureRoute>} />
          <Route path="/payroll" element={<FeatureRoute module="payroll" enabledModules={enabledModules}><PayrollDashboardPage /></FeatureRoute>} />
          <Route path="/projects" element={<FeatureRoute module="project" enabledModules={enabledModules}><ProjectDashboardPage /></FeatureRoute>} />
          <Route path="/documents" element={<FeatureRoute module="document" enabledModules={enabledModules}><DocumentDashboardPage /></FeatureRoute>} />
          <Route path="/reports" element={<FeatureRoute module="report" enabledModules={enabledModules}><ReportDashboardPage /></FeatureRoute>} />
          <Route path="/crm" element={<FeatureRoute module="crm" enabledModules={enabledModules}><CrmDashboardPage /></FeatureRoute>} />
          <Route path="/inventory" element={<FeatureRoute module="inventory" enabledModules={enabledModules}><InventoryDashboardPage /></FeatureRoute>} />
          <Route path="/meetings" element={<FeatureRoute module="meeting" enabledModules={enabledModules}><MeetingDashboardPage /></FeatureRoute>} />
          <Route path="/announcements" element={<FeatureRoute module="announcement" enabledModules={enabledModules}><AnnouncementDashboardPage /></FeatureRoute>} />

          {/* 預設導向登入頁面 */}
          <Route path="/" element={<Navigate to={defaultPath} replace />} />
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </Router>
    </>
  );
}

export default App;
