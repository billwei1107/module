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

const AppLayout = ({ children }: { children: React.ReactNode }) => (
  <Box sx={{ flexGrow: 1, minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          模塊化企業系統
        </Typography>
        <NotificationBell />
        <Button color="inherit" component={Link} to="/department">組織管理</Button>
        <Button color="inherit" component={Link} to="/employee">員工管理</Button>
        <Button color="inherit" component={Link} to="/workflow">發起簽核</Button>
        <Button color="inherit" component={Link} to="/my-tasks">我的待辦</Button>
        <Button color="inherit" component={Link} to="/attendance/clock-in">打卡</Button>
        <Button color="inherit" component={Link} to="/attendance/records">出勤記錄</Button>
        <Button color="inherit" component={Link} to="/leave/requests">請假</Button>
        <Button color="inherit" component={Link} to="/leave/approval">請假審核</Button>
        <Button color="inherit" component={Link} to="/system">系統設定</Button>
        <Button color="inherit" component={Link} to="/audit/logs">稽核日誌</Button>
        <Button color="inherit" component={Link} to="/finance">財務</Button>
        <Button color="inherit" component={Link} to="/payroll">薪資</Button>
        <Button color="inherit" component={Link} to="/projects">專案</Button>
        <Button color="inherit" component={Link} to="/login">登出</Button>
      </Toolbar>
    </AppBar>
    <Container sx={{ mt: 4, flexGrow: 1 }}>
      {children}
    </Container>
  </Box>
);

function App() {
  return (
    <>
      <CssBaseline />
      <Router>
        <Routes>
          <Route path="/login" element={<LoginPage />} />

          {/* 受保護的後台區域 */}
          <Route path="/role" element={<AppLayout><RoleListPage /></AppLayout>} />
          <Route path="/company" element={<AppLayout><CompanyPage /></AppLayout>} />
          <Route path="/department" element={<AppLayout><DepartmentPage /></AppLayout>} />
          <Route path="/position" element={<AppLayout><PositionPage /></AppLayout>} />
          <Route path="/employee" element={<AppLayout><EmployeeListPage /></AppLayout>} />
          <Route path="/workflow" element={<AppLayout><DefinitionListPage /></AppLayout>} />
          <Route path="/my-tasks" element={<AppLayout><MyTasksPage /></AppLayout>} />
          <Route path="/attendance/clock-in" element={<AppLayout><ClockInPage /></AppLayout>} />
          <Route path="/attendance/records" element={<AppLayout><AttendanceRecordsPage /></AppLayout>} />
          <Route path="/attendance/shifts" element={<AppLayout><ShiftManagementPage /></AppLayout>} />
          <Route path="/attendance/report" element={<AppLayout><AttendanceReportPage /></AppLayout>} />
          <Route path="/leave/requests" element={<AppLayout><LeaveRequestPage /></AppLayout>} />
          <Route path="/leave/approval" element={<AppLayout><LeaveApprovalPage /></AppLayout>} />
          <Route path="/system" element={<AppLayout><SystemSettingsPage /></AppLayout>} />
          <Route path="/audit/logs" element={<AppLayout><AuditLogPage /></AppLayout>} />
          <Route path="/finance" element={<AppLayout><FinanceDashboardPage /></AppLayout>} />
          <Route path="/payroll" element={<AppLayout><PayrollDashboardPage /></AppLayout>} />
          <Route path="/projects" element={<AppLayout><ProjectDashboardPage /></AppLayout>} />

          {/* 預設導向登入頁面 */}
          <Route path="/" element={<Navigate to="/department" replace />} />
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </Router>
    </>
  );
}

export default App;
