package com.enterprise.leave.service.impl;

import com.enterprise.leave.dto.CreateLeaveRequest;
import com.enterprise.leave.dto.LeaveRequestDTO;
import com.enterprise.leave.entity.LeaveBalance;
import com.enterprise.leave.entity.LeaveType;
import com.enterprise.leave.repository.LeaveBalanceRepository;
import com.enterprise.leave.repository.LeaveRequestRepository;
import com.enterprise.leave.repository.LeaveTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @file LeaveServiceImplTest.java
 * @description 請假服務測試 / Leave service tests
 */
class LeaveServiceImplTest {

    private LeaveTypeRepository leaveTypeRepository;
    private LeaveBalanceRepository leaveBalanceRepository;
    private LeaveRequestRepository leaveRequestRepository;
    private LeaveServiceImpl leaveService;

    private LeaveType annualLeave;

    @BeforeEach
    void setUp() {
        leaveTypeRepository = mock(LeaveTypeRepository.class);
        leaveBalanceRepository = mock(LeaveBalanceRepository.class);
        leaveRequestRepository = mock(LeaveRequestRepository.class);
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        leaveService = new LeaveServiceImpl(
                leaveTypeRepository, leaveBalanceRepository, leaveRequestRepository, eventPublisher);

        annualLeave = new LeaveType();
        annualLeave.setId(UUID.randomUUID());
        annualLeave.setName("特休");
        annualLeave.setCode("ANNUAL");
        annualLeave.setAnnualQuotaHours(112);
        annualLeave.setRequiresApproval(true);
    }

    @Test
    void submitLeaveRequestShouldReserveBalanceAndReturnPendingRequest() {
        CreateLeaveRequest request = new CreateLeaveRequest();
        request.setEmployeeId("emp-001");
        request.setLeaveTypeId(annualLeave.getId().toString());
        request.setStartTime(LocalDateTime.of(2026, 5, 11, 9, 0));
        request.setEndTime(LocalDateTime.of(2026, 5, 11, 18, 0));
        request.setReason("家庭照顧");

        LeaveBalance balance = new LeaveBalance();
        balance.setEmployeeId("emp-001");
        balance.setLeaveTypeId(annualLeave.getId());
        balance.setYear(2026);
        balance.setTotalHours(112);
        balance.setUsedHours(0);
        balance.setReservedHours(0);

        when(leaveTypeRepository.findById(annualLeave.getId())).thenReturn(Optional.of(annualLeave));
        when(leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYearAndDeletedAtIsNull(
                "emp-001", annualLeave.getId(), 2026)).thenReturn(Optional.of(balance));
        when(leaveRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        LeaveRequestDTO result = leaveService.submitLeaveRequest(request);

        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getHours()).isEqualTo(9);
        assertThat(balance.getReservedHours()).isEqualTo(9);
        verify(leaveBalanceRepository).save(balance);
    }
}
