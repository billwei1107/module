package com.enterprise.payroll.repository;

import com.enterprise.payroll.entity.PayrollAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @file PayrollAdjustmentRepository.java
 * @description 薪資調整資料存取 / Payroll adjustment repository
 */
@Repository
public interface PayrollAdjustmentRepository extends JpaRepository<PayrollAdjustment, UUID> {
    List<PayrollAdjustment> findByEmployeeIdAndYearMonthAndDeletedAtIsNull(String employeeId, String yearMonth);
}
