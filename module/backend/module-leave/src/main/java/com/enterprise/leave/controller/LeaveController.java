package com.enterprise.leave.controller;

import com.enterprise.common.dto.ApiResponse;
import com.enterprise.common.security.SecurityUtils;
import com.enterprise.leave.dto.CreateLeaveRequest;
import com.enterprise.leave.dto.LeaveBalanceDTO;
import com.enterprise.leave.dto.LeaveRequestDTO;
import com.enterprise.leave.dto.LeaveTypeDTO;
import com.enterprise.leave.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * @file LeaveController.java
 * @description 請假管理控制器 / Leave management controller
 * @description_zh 處理假別查詢、請假申請、待審核清單與審核動作
 */
@RestController
@RequestMapping("/api/v1/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    // ========================================
    // 假別查詢 / Leave Types
    // ========================================
    @GetMapping("/types")
    public ApiResponse<List<LeaveTypeDTO>> getLeaveTypes() {
        return ApiResponse.success(leaveService.getActiveLeaveTypes());
    }

    // ========================================
    // 員工配額 / Employee Balances
    // ========================================
    @GetMapping("/balances")
    public ApiResponse<List<LeaveBalanceDTO>> getBalances(
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) Integer year) {
        String targetEmployeeId = employeeId != null ? employeeId : SecurityUtils.getCurrentUserId();
        int targetYear = year != null ? year : LocalDate.now().getYear();
        return ApiResponse.success(leaveService.getEmployeeBalances(targetEmployeeId, targetYear));
    }

    // ========================================
    // 建立申請 / Submit Request
    // ========================================
    @PostMapping("/requests")
    public ApiResponse<LeaveRequestDTO> submitLeaveRequest(@RequestBody CreateLeaveRequest request) {
        if (request.getEmployeeId() == null) {
            request.setEmployeeId(SecurityUtils.getCurrentUserId());
        }
        return ApiResponse.success(leaveService.submitLeaveRequest(request));
    }

    // ========================================
    // 我的申請 / My Requests
    // ========================================
    @GetMapping("/requests")
    public ApiResponse<List<LeaveRequestDTO>> getMyRequests(@RequestParam(required = false) String employeeId) {
        String targetEmployeeId = employeeId != null ? employeeId : SecurityUtils.getCurrentUserId();
        return ApiResponse.success(leaveService.getEmployeeRequests(targetEmployeeId));
    }

    // ========================================
    // 待審核清單 / Pending Requests
    // ========================================
    @GetMapping("/requests/pending")
    public ApiResponse<List<LeaveRequestDTO>> getPendingRequests() {
        return ApiResponse.success(leaveService.getPendingRequests());
    }

    // ========================================
    // 審核動作 / Review Actions
    // ========================================
    @PostMapping("/requests/{id}/approve")
    public ApiResponse<LeaveRequestDTO> approve(@PathVariable String id) {
        return ApiResponse.success(leaveService.approveLeaveRequest(id, SecurityUtils.getCurrentUserId()));
    }

    @PostMapping("/requests/{id}/reject")
    public ApiResponse<LeaveRequestDTO> reject(@PathVariable String id) {
        return ApiResponse.success(leaveService.rejectLeaveRequest(id, SecurityUtils.getCurrentUserId()));
    }
}
