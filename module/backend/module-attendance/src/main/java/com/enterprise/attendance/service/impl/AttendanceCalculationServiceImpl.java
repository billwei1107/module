/**
 * @file AttendanceCalculationServiceImpl.java
 * @description 考勤計算服務實作 / Attendance calculation service implementation
 * @description_en Calculates overtime and late minutes, publishes OvertimeRecordedEvent when threshold exceeded
 * @description_zh 計算加班分鐘數與遲到分鐘數，超過門檻時發佈加班事件
 */
package com.enterprise.attendance.service.impl;

import com.enterprise.attendance.entity.AttendanceRecord;
import com.enterprise.attendance.entity.ShiftSchedule;
import com.enterprise.attendance.event.OvertimeRecordedEvent;
import com.enterprise.attendance.service.AttendanceCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceCalculationServiceImpl implements AttendanceCalculationService {

    private static final int OVERTIME_THRESHOLD_MINUTES = 30;

    private final ApplicationEventPublisher eventPublisher;

    // ========================================
    // 加班計算 / Overtime Calculation
    // ========================================
    @Override
    public int calculateOvertime(AttendanceRecord record, ShiftSchedule shift) {
        if (record.getClockOutTime() == null) return 0;

        LocalTime clockOut = record.getClockOutTime().toLocalTime();
        LocalTime shiftEnd = shift.getEndTime();

        if (!clockOut.isAfter(shiftEnd)) return 0;

        int overtimeMinutes = (int) Duration.between(shiftEnd, clockOut).toMinutes();

        if (overtimeMinutes >= OVERTIME_THRESHOLD_MINUTES) {
            eventPublisher.publishEvent(new OvertimeRecordedEvent(
                    this,
                    record.getId(),
                    record.getEmployeeId(),
                    record.getDate(),
                    overtimeMinutes
            ));
            log.info("💼 OvertimeRecordedEvent published: employee={}, minutes={}",
                    record.getEmployeeId(), overtimeMinutes);
        }

        return overtimeMinutes;
    }

    // ========================================
    // 遲到計算 / Late Minute Calculation
    // ========================================
    @Override
    public int calculateLateMinutes(LocalTime clockInTime, LocalTime shiftStartTime, int graceMinutes) {
        LocalTime deadline = shiftStartTime.plusMinutes(graceMinutes);
        if (!clockInTime.isAfter(deadline)) return 0;
        return (int) Duration.between(deadline, clockInTime).toMinutes();
    }
}
