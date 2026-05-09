package com.enterprise.leave.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

/**
 * @file LeaveRequestedEvent.java
 * @description 請假送審事件 / Leave requested event
 * @description_zh 請假申請建立後發佈，供通知與流程模組監聽
 */
@Getter
public class LeaveRequestedEvent extends ApplicationEvent {
    private final UUID requestId;
    private final String employeeId;
    private final Integer hours;

    public LeaveRequestedEvent(Object source, UUID requestId, String employeeId, Integer hours) {
        super(source);
        this.requestId = requestId;
        this.employeeId = employeeId;
        this.hours = hours;
    }
}
