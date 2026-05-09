package com.enterprise.payroll.repository;

import com.enterprise.payroll.entity.PayrollRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @file PayrollRecordRepository.java
 * @description 薪資紀錄資料存取 / Payroll record repository
 */
@Repository
public interface PayrollRecordRepository extends JpaRepository<PayrollRecord, UUID> {
    Optional<PayrollRecord> findByEmployeeIdAndYearMonthAndDeletedAtIsNull(String employeeId, String yearMonth);

    List<PayrollRecord> findByYearMonthAndDeletedAtIsNullOrderByEmployeeIdAsc(String yearMonth);

    List<PayrollRecord> findByEmployeeIdAndDeletedAtIsNullOrderByYearMonthDesc(String employeeId);

    List<PayrollRecord> findByDeletedAtIsNullOrderByYearMonthDescEmployeeIdAsc();
}
