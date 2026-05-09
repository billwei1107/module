package com.enterprise.leave.repository;

import com.enterprise.leave.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @file LeaveTypeRepository.java
 * @description 假別資料存取 / Leave type repository
 */
@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType, UUID> {
    Optional<LeaveType> findByCodeAndDeletedAtIsNull(String code);

    List<LeaveType> findByActiveTrueAndDeletedAtIsNullOrderByCodeAsc();
}
