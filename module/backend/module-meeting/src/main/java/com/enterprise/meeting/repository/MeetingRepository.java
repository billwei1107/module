package com.enterprise.meeting.repository;

import com.enterprise.meeting.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface MeetingRepository extends JpaRepository<Meeting, UUID> {
    List<Meeting> findByDeletedAtIsNullOrderByStartTimeDesc();
}
