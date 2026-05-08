/**
 * @file AttendanceCalculationServiceTest.java
 * @description 考勤計算服務單元測試 / Attendance calculation service unit tests
 * @description_en Tests for overtime and late minute calculation logic
 * @description_zh 測試加班分鐘數與遲到分鐘數計算邏輯
 */
package com.enterprise.attendance;

import com.enterprise.attendance.entity.AttendanceRecord;
import com.enterprise.attendance.entity.ShiftSchedule;
import com.enterprise.attendance.event.OvertimeRecordedEvent;
import com.enterprise.attendance.service.impl.AttendanceCalculationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceCalculationServiceTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private AttendanceCalculationServiceImpl calculationService;

    private ShiftSchedule shift;

    @BeforeEach
    void setUp() {
        calculationService = new AttendanceCalculationServiceImpl(eventPublisher);

        shift = new ShiftSchedule();
        shift.setName("Standard");
        shift.setStartTime(LocalTime.of(9, 0));
        shift.setEndTime(LocalTime.of(18, 0));
        shift.setGraceMinutes(5);
    }

    // ========================================
    // 遲到計算 / Late Minute Tests
    // ========================================

    @Test
    void calculateLateMinutes_withinGrace_returnsZero() {
        // 09:03 打卡，寬限 5 分 → 不遲到
        int lateMinutes = calculationService.calculateLateMinutes(
                LocalTime.of(9, 3), LocalTime.of(9, 0), 5);
        assertThat(lateMinutes).isEqualTo(0);
    }

    @Test
    void calculateLateMinutes_exactlyAtDeadline_returnsZero() {
        // 09:05 打卡，寬限 5 分，deadline = 09:05 → 不遲到
        int lateMinutes = calculationService.calculateLateMinutes(
                LocalTime.of(9, 5), LocalTime.of(9, 0), 5);
        assertThat(lateMinutes).isEqualTo(0);
    }

    @Test
    void calculateLateMinutes_afterGrace_returnsCorrectMinutes() {
        // 09:06 打卡，寬限 5 分，deadline = 09:05 → 遲到 1 分
        int lateMinutes = calculationService.calculateLateMinutes(
                LocalTime.of(9, 6), LocalTime.of(9, 0), 5);
        assertThat(lateMinutes).isEqualTo(1);
    }

    @Test
    void calculateLateMinutes_thirtyMinutesLate_returnsThirty() {
        // 09:35 打卡，寬限 5 分 → 遲到 30 分
        int lateMinutes = calculationService.calculateLateMinutes(
                LocalTime.of(9, 35), LocalTime.of(9, 0), 5);
        assertThat(lateMinutes).isEqualTo(30);
    }

    // ========================================
    // 加班計算 / Overtime Tests
    // ========================================

    @Test
    void calculateOvertime_noOvertime_returnsZero() {
        AttendanceRecord record = buildRecord("emp-001", LocalTime.of(18, 0));
        int overtime = calculationService.calculateOvertime(record, shift);
        assertThat(overtime).isEqualTo(0);
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void calculateOvertime_belowThreshold_noEventPublished() {
        // 加班 20 分鐘，未達 30 分鐘門檻 → 不發佈事件
        AttendanceRecord record = buildRecord("emp-001", LocalTime.of(18, 20));
        int overtime = calculationService.calculateOvertime(record, shift);
        assertThat(overtime).isEqualTo(20);
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void calculateOvertime_exactlyThreshold_eventPublished() {
        // 加班 30 分鐘，達門檻 → 發佈 OvertimeRecordedEvent
        AttendanceRecord record = buildRecord("emp-001", LocalTime.of(18, 30));
        int overtime = calculationService.calculateOvertime(record, shift);
        assertThat(overtime).isEqualTo(30);

        ArgumentCaptor<OvertimeRecordedEvent> captor = ArgumentCaptor.forClass(OvertimeRecordedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().getEmployeeId()).isEqualTo("emp-001");
        assertThat(captor.getValue().getOvertimeMinutes()).isEqualTo(30);
    }

    @Test
    void calculateOvertime_aboveThreshold_eventPublishedWithCorrectMinutes() {
        // 加班 90 分鐘 → 發佈事件，分鐘數正確
        AttendanceRecord record = buildRecord("emp-002", LocalTime.of(19, 30));
        int overtime = calculationService.calculateOvertime(record, shift);
        assertThat(overtime).isEqualTo(90);

        ArgumentCaptor<OvertimeRecordedEvent> captor = ArgumentCaptor.forClass(OvertimeRecordedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().getOvertimeMinutes()).isEqualTo(90);
    }

    @Test
    void calculateOvertime_noClockOut_returnsZero() {
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId("emp-001");
        record.setDate(LocalDate.now());
        // clockOutTime = null
        int overtime = calculationService.calculateOvertime(record, shift);
        assertThat(overtime).isEqualTo(0);
        verify(eventPublisher, never()).publishEvent(any());
    }

    // ========================================
    // Helper / 輔助方法
    // ========================================

    private AttendanceRecord buildRecord(String employeeId, LocalTime clockOutTime) {
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId(employeeId);
        record.setDate(LocalDate.now());
        record.setClockInTime(LocalDateTime.now().withHour(9).withMinute(0));
        record.setClockOutTime(LocalDate.now().atTime(clockOutTime));
        return record;
    }
}
