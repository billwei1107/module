package com.enterprise.payroll.repository;

import com.enterprise.payroll.entity.PayrollDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @file PayrollDetailRepository.java
 * @description 薪資明細資料存取 / Payroll detail repository
 */
@Repository
public interface PayrollDetailRepository extends JpaRepository<PayrollDetail, UUID> {
    List<PayrollDetail> findByPayrollRecordIdAndDeletedAtIsNull(UUID payrollRecordId);

    void deleteByPayrollRecordId(UUID payrollRecordId);
}
