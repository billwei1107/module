package com.enterprise.meeting.repository;

import com.enterprise.meeting.entity.MeetingMinute;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface MeetingMinuteRepository extends JpaRepository<MeetingMinute, UUID> {
}
