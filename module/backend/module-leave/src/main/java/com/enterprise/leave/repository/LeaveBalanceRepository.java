package com.enterprise.leave.repository;

import com.enterprise.leave.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @file LeaveBalanceRepository.java
 * @description 員工假別配額資料存取 / Leave balance repository
 */
@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, UUID> {
    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndYearAndDeletedAtIsNull(
            String employeeId, UUID leaveTypeId, Integer year);

    List<LeaveBalance> findByEmployeeIdAndYearAndDeletedAtIsNullOrderByLeaveTypeIdAsc(String employeeId, Integer year);
}
