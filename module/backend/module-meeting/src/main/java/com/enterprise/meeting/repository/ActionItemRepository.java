package com.enterprise.meeting.repository;

import com.enterprise.meeting.entity.ActionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ActionItemRepository extends JpaRepository<ActionItem, UUID> {
    List<ActionItem> findByMeetingIdAndDeletedAtIsNullOrderByCreatedAtAsc(UUID meetingId);
}
