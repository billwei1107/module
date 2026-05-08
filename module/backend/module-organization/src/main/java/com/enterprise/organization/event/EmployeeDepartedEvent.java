/**
 * @file EmployeeDepartedEvent.java
 * @description 員工離職事件 / Employee departed event
 * @description_en Published when an employee is marked as departed/terminated
 * @description_zh 員工離職或終止時發佈，供考勤等模塊取消未來排班
 */
package com.enterprise.organization.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EmployeeDepartedEvent extends ApplicationEvent {

    private final String employeeId;
    private final String employeeName;

    public EmployeeDepartedEvent(Object source, String employeeId, String employeeName) {
        super(source);
        this.employeeId = employeeId;
        this.employeeName = employeeName;
    }
}
