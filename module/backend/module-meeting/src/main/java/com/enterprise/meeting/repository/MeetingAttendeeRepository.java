package com.enterprise.meeting.repository;

import com.enterprise.meeting.entity.MeetingAttendee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface MeetingAttendeeRepository extends JpaRepository<MeetingAttendee, UUID> {
    List<MeetingAttendee> findByMeetingIdAndDeletedAtIsNullOrderByCreatedAtAsc(UUID meetingId);
}
