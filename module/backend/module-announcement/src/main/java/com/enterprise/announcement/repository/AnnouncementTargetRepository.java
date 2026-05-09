package com.enterprise.announcement.repository;

import com.enterprise.announcement.entity.AnnouncementTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AnnouncementTargetRepository extends JpaRepository<AnnouncementTarget, UUID> {
    List<AnnouncementTarget> findByAnnouncementIdAndDeletedAtIsNullOrderByCreatedAtAsc(UUID announcementId);
}
