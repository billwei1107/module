package com.enterprise.leave.repository;

import com.enterprise.leave.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @file LeaveRequestRepository.java
 * @description 請假申請資料存取 / Leave request repository
 */
@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, UUID> {
    List<LeaveRequest> findByEmployeeIdAndDeletedAtIsNullOrderByStartTimeDesc(String employeeId);

    List<LeaveRequest> findByStatusAndDeletedAtIsNullOrderByCreatedAtAsc(String status);
}
