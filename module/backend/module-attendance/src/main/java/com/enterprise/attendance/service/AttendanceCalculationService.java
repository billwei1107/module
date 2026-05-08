/**
 * @file AttendanceCalculationService.java
 * @description 考勤計算服務介面 / Attendance calculation service interface
 * @description_en Interface for overtime and late-minute calculations
 * @description_zh 定義加班分鐘數與遲到分鐘數計算的服務介面
 */
package com.enterprise.attendance.service;

import com.enterprise.attendance.entity.AttendanceRecord;
import com.enterprise.attendance.entity.ShiftSchedule;

import java.time.LocalTime;

public interface AttendanceCalculationService {

    /**
     * 計算加班分鐘數，超過門檻發佈 OvertimeRecordedEvent / Calculate overtime and publish event if threshold exceeded
     */
    int calculateOvertime(AttendanceRecord record, ShiftSchedule shift);

    /**
     * 計算遲到分鐘數（已扣除寬限時間）/ Calculate late minutes after grace period
     */
    int calculateLateMinutes(LocalTime clockInTime, LocalTime shiftStartTime, int graceMinutes);
}
