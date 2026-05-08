/**
 * @file EmployeeEventListener.java
 * @description 員工事件監聽器 / Employee event listener
 * @description_en Listens to EmployeeCreatedEvent to assign default shift, and EmployeeDepartedEvent to cancel future shifts
 * @description_zh 監聽員工建立事件自動指派預設班表，監聽員工離職事件取消未來班表指派
 */
package com.enterprise.attendance.listener;

import com.enterprise.attendance.entity.ShiftAssignment;
import com.enterprise.attendance.repository.ShiftAssignmentRepository;
import com.enterprise.attendance.repository.ShiftScheduleRepository;
import com.enterprise.organization.event.EmployeeCreatedEvent;
import com.enterprise.organization.event.EmployeeDepartedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeEventListener {

    private final ShiftScheduleRepository shiftScheduleRepository;
    private final ShiftAssignmentRepository shiftAssignmentRepository;

    // ========================================
    // 員工建立 → 指派預設班表 / Assign default shift on employee created
    // ========================================
    @EventListener
    @Transactional
    public void onEmployeeCreated(EmployeeCreatedEvent event) {
        // 取第一筆可用班表作為預設 / Use first available shift as default
        shiftScheduleRepository.findAll().stream()
                .filter(s -> s.getDeletedAt() == null)
                .findFirst()
                .ifPresent(defaultShift -> {
                    ShiftAssignment assignment = new ShiftAssignment();
                    assignment.setEmployeeId(event.getEmployee().getId().toString());
                    assignment.setShiftScheduleId(defaultShift.getId());
                    assignment.setEffectiveDate(LocalDate.now());
                    shiftAssignmentRepository.save(assignment);
                    log.info("📋 Auto-assigned default shift '{}' to new employee {}",
                            defaultShift.getName(), event.getEmployee().getId());
                });
    }

    // ========================================
    // 員工離職 → 取消未來班表 / Cancel future shifts on employee departed
    // ========================================
    @EventListener
    @Transactional
    public void onEmployeeDeparted(EmployeeDepartedEvent event) {
        shiftAssignmentRepository.findByEmployeeIdAndDeletedAtIsNull(event.getEmployeeId().toString()).stream()
                .filter(sa -> sa.getEndDate() == null || sa.getEndDate().isAfter(LocalDate.now()))
                .forEach(sa -> {
                    sa.setEndDate(LocalDate.now());
                    sa.setDeletedAt(LocalDateTime.now());
                    shiftAssignmentRepository.save(sa);
                });
        log.info("🚪 Cancelled future shifts for departed employee {}", event.getEmployeeId());
    }
}
