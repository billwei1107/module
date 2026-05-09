package com.enterprise.leave.service.impl;

import com.enterprise.common.exception.BusinessException;
import com.enterprise.leave.dto.CreateLeaveRequest;
import com.enterprise.leave.dto.LeaveBalanceDTO;
import com.enterprise.leave.dto.LeaveRequestDTO;
import com.enterprise.leave.dto.LeaveTypeDTO;
import com.enterprise.leave.entity.LeaveBalance;
import com.enterprise.leave.entity.LeaveRequest;
import com.enterprise.leave.entity.LeaveType;
import com.enterprise.leave.event.LeaveRequestedEvent;
import com.enterprise.leave.repository.LeaveBalanceRepository;
import com.enterprise.leave.repository.LeaveRequestRepository;
import com.enterprise.leave.repository.LeaveTypeRepository;
import com.enterprise.leave.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @file LeaveServiceImpl.java
 * @description 請假管理服務實作 / Leave management service implementation
 * @description_zh 處理請假申請、配額保留、審核通過與駁回
 */
@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final ApplicationEventPublisher eventPublisher;

    // ========================================
    // 建立請假申請 / Submit Leave Request
    // ========================================
    @Override
    @Transactional
    public LeaveRequestDTO submitLeaveRequest(CreateLeaveRequest request) {
        validateRequestTime(request.getStartTime(), request.getEndTime());

        UUID leaveTypeId = UUID.fromString(request.getLeaveTypeId());
        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new BusinessException(404, "假別不存在 / Leave type not found"));

        int hours = calculateLeaveHours(request.getStartTime(), request.getEndTime());
        LeaveBalance balance = findOrCreateBalance(request.getEmployeeId(), leaveType, request.getStartTime().getYear());
        int availableHours = balance.getTotalHours() - balance.getUsedHours() - balance.getReservedHours();
        if (availableHours < hours) {
            throw new BusinessException(409, "假別配額不足 / Insufficient leave balance");
        }

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployeeId(request.getEmployeeId());
        leaveRequest.setLeaveTypeId(leaveTypeId);
        leaveRequest.setStartTime(request.getStartTime());
        leaveRequest.setEndTime(request.getEndTime());
        leaveRequest.setHours(hours);
        leaveRequest.setReason(request.getReason());
        leaveRequest.setStatus(leaveType.getRequiresApproval() ? "PENDING" : "APPROVED");

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);
        if ("PENDING".equals(saved.getStatus())) {
            balance.setReservedHours(balance.getReservedHours() + hours);
            eventPublisher.publishEvent(new LeaveRequestedEvent(this, saved.getId(), saved.getEmployeeId(), hours));
        } else {
            balance.setUsedHours(balance.getUsedHours() + hours);
        }
        leaveBalanceRepository.save(balance);

        return toRequestDTO(saved);
    }

    // ========================================
    // 審核通過 / Approve Leave Request
    // ========================================
    @Override
    @Transactional
    public LeaveRequestDTO approveLeaveRequest(String requestId, String reviewerId) {
        LeaveRequest request = findPendingRequest(requestId);
        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndYearAndDeletedAtIsNull(
                        request.getEmployeeId(), request.getLeaveTypeId(), request.getStartTime().getYear())
                .orElseThrow(() -> new BusinessException(404, "找不到請假配額 / Leave balance not found"));

        balance.setReservedHours(Math.max(0, balance.getReservedHours() - request.getHours()));
        balance.setUsedHours(balance.getUsedHours() + request.getHours());
        request.setStatus("APPROVED");
        request.setReviewedBy(reviewerId);
        request.setReviewedAt(LocalDateTime.now());

        leaveBalanceRepository.save(balance);
        return toRequestDTO(leaveRequestRepository.save(request));
    }

    // ========================================
    // 駁回申請 / Reject Leave Request
    // ========================================
    @Override
    @Transactional
    public LeaveRequestDTO rejectLeaveRequest(String requestId, String reviewerId) {
        LeaveRequest request = findPendingRequest(requestId);
        leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYearAndDeletedAtIsNull(
                        request.getEmployeeId(), request.getLeaveTypeId(), request.getStartTime().getYear())
                .ifPresent(balance -> {
                    balance.setReservedHours(Math.max(0, balance.getReservedHours() - request.getHours()));
                    leaveBalanceRepository.save(balance);
                });

        request.setStatus("REJECTED");
        request.setReviewedBy(reviewerId);
        request.setReviewedAt(LocalDateTime.now());
        return toRequestDTO(leaveRequestRepository.save(request));
    }

    @Override
    public List<LeaveRequestDTO> getEmployeeRequests(String employeeId) {
        return leaveRequestRepository.findByEmployeeIdAndDeletedAtIsNullOrderByStartTimeDesc(employeeId)
                .stream().map(this::toRequestDTO).toList();
    }

    @Override
    public List<LeaveRequestDTO> getPendingRequests() {
        return leaveRequestRepository.findByStatusAndDeletedAtIsNullOrderByCreatedAtAsc("PENDING")
                .stream().map(this::toRequestDTO).toList();
    }

    @Override
    public List<LeaveTypeDTO> getActiveLeaveTypes() {
        return leaveTypeRepository.findByActiveTrueAndDeletedAtIsNullOrderByCodeAsc()
                .stream().map(this::toTypeDTO).toList();
    }

    @Override
    public List<LeaveBalanceDTO> getEmployeeBalances(String employeeId, int year) {
        return leaveBalanceRepository.findByEmployeeIdAndYearAndDeletedAtIsNullOrderByLeaveTypeIdAsc(employeeId, year)
                .stream().map(this::toBalanceDTO).toList();
    }

    private LeaveRequest findPendingRequest(String requestId) {
        LeaveRequest request = leaveRequestRepository.findById(UUID.fromString(requestId))
                .orElseThrow(() -> new BusinessException(404, "請假申請不存在 / Leave request not found"));
        if (!"PENDING".equals(request.getStatus())) {
            throw new BusinessException(409, "請假申請已處理 / Leave request already processed");
        }
        return request;
    }

    private LeaveBalance findOrCreateBalance(String employeeId, LeaveType leaveType, int year) {
        return leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYearAndDeletedAtIsNull(
                employeeId, leaveType.getId(), year).orElseGet(() -> {
            LeaveBalance balance = new LeaveBalance();
            balance.setEmployeeId(employeeId);
            balance.setLeaveTypeId(leaveType.getId());
            balance.setYear(year);
            balance.setTotalHours(leaveType.getAnnualQuotaHours());
            return balance;
        });
    }

    private void validateRequestTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null || !endTime.isAfter(startTime)) {
            throw new BusinessException(400, "請假時間不正確 / Invalid leave period");
        }
    }

    private int calculateLeaveHours(LocalDateTime startTime, LocalDateTime endTime) {
        long minutes = Duration.between(startTime, endTime).toMinutes();
        return Math.max(1, (int) Math.ceil(minutes / 60.0));
    }

    private LeaveRequestDTO toRequestDTO(LeaveRequest request) {
        return LeaveRequestDTO.builder()
                .id(request.getId() != null ? request.getId().toString() : null)
                .employeeId(request.getEmployeeId())
                .leaveTypeId(request.getLeaveTypeId().toString())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .hours(request.getHours())
                .reason(request.getReason())
                .status(request.getStatus())
                .workflowInstanceId(request.getWorkflowInstanceId())
                .build();
    }

    private LeaveTypeDTO toTypeDTO(LeaveType leaveType) {
        return LeaveTypeDTO.builder()
                .id(leaveType.getId() != null ? leaveType.getId().toString() : null)
                .name(leaveType.getName())
                .code(leaveType.getCode())
                .annualQuotaHours(leaveType.getAnnualQuotaHours())
                .requiresApproval(leaveType.getRequiresApproval())
                .paid(leaveType.getPaid())
                .active(leaveType.getActive())
                .build();
    }

    private LeaveBalanceDTO toBalanceDTO(LeaveBalance balance) {
        int availableHours = balance.getTotalHours() - balance.getUsedHours() - balance.getReservedHours();
        return LeaveBalanceDTO.builder()
                .id(balance.getId() != null ? balance.getId().toString() : null)
                .employeeId(balance.getEmployeeId())
                .leaveTypeId(balance.getLeaveTypeId().toString())
                .year(balance.getYear())
                .totalHours(balance.getTotalHours())
                .usedHours(balance.getUsedHours())
                .reservedHours(balance.getReservedHours())
                .availableHours(availableHours)
                .build();
    }
}
