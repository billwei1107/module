package com.enterprise.leave.service;

import com.enterprise.leave.dto.CreateLeaveRequest;
import com.enterprise.leave.dto.LeaveBalanceDTO;
import com.enterprise.leave.dto.LeaveRequestDTO;
import com.enterprise.leave.dto.LeaveTypeDTO;

import java.util.List;

/**
 * @file LeaveService.java
 * @description 請假管理服務介面 / Leave management service interface
 */
public interface LeaveService {

    LeaveRequestDTO submitLeaveRequest(CreateLeaveRequest request);

    LeaveRequestDTO approveLeaveRequest(String requestId, String reviewerId);

    LeaveRequestDTO rejectLeaveRequest(String requestId, String reviewerId);

    List<LeaveRequestDTO> getEmployeeRequests(String employeeId);

    List<LeaveRequestDTO> getPendingRequests();

    List<LeaveTypeDTO> getActiveLeaveTypes();

    List<LeaveBalanceDTO> getEmployeeBalances(String employeeId, int year);
}
