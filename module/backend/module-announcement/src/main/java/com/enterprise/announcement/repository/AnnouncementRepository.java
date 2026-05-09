package com.enterprise.announcement.repository;

import com.enterprise.announcement.entity.Announcement;
import com.enterprise.announcement.entity.Announcement.AnnouncementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AnnouncementRepository extends JpaRepository<Announcement, UUID> {
    List<Announcement> findByDeletedAtIsNullOrderByCreatedAtDesc();
    List<Announcement> findByStatusAndDeletedAtIsNullOrderByScheduledPublishAtDesc(AnnouncementStatus status);
    List<Announcement> findByStatusAndScheduledPublishAtLessThanEqualAndDeletedAtIsNull(AnnouncementStatus status, LocalDateTime now);
    List<Announcement> findByStatusAndScheduledUnpublishAtLessThanEqualAndDeletedAtIsNull(AnnouncementStatus status, LocalDateTime now);
}
