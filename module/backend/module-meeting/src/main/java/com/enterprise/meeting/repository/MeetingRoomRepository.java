package com.enterprise.meeting.repository;

import com.enterprise.meeting.entity.MeetingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, UUID> {
    List<MeetingRoom> findByDeletedAtIsNullOrderByNameAsc();
}
