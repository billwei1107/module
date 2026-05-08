/**
 * @file OvertimeRecordedEvent.java
 * @description 加班記錄事件 / Overtime recorded event
 * @description_en Published when an employee's overtime exceeds the threshold (30 minutes)
 * @description_zh 當員工加班超過門檻（30 分鐘）時發佈此事件
 */
package com.enterprise.attendance.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class OvertimeRecordedEvent extends ApplicationEvent {

    private final UUID recordId;
    private final String employeeId;
    private final LocalDate date;
    private final int overtimeMinutes;

    public OvertimeRecordedEvent(Object source, UUID recordId, String employeeId,
                                  LocalDate date, int overtimeMinutes) {
        super(source);
        this.recordId = recordId;
        this.employeeId = employeeId;
        this.date = date;
        this.overtimeMinutes = overtimeMinutes;
    }
}
