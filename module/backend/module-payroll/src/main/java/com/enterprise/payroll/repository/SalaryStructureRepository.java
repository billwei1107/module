package com.enterprise.payroll.repository;

import com.enterprise.payroll.entity.SalaryStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @file SalaryStructureRepository.java
 * @description 薪資結構資料存取 / Salary structure repository
 */
@Repository
public interface SalaryStructureRepository extends JpaRepository<SalaryStructure, UUID> {
    Optional<SalaryStructure> findByEmployeeIdAndActiveTrueAndDeletedAtIsNull(String employeeId);

    List<SalaryStructure> findByDeletedAtIsNullOrderByEmployeeIdAsc();
}
