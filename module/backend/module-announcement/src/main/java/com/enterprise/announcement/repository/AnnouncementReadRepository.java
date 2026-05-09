package com.enterprise.announcement.repository;

import com.enterprise.announcement.entity.AnnouncementRead;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface AnnouncementReadRepository extends JpaRepository<AnnouncementRead, UUID> {
    Optional<AnnouncementRead> findByAnnouncementIdAndUserIdAndDeletedAtIsNull(UUID announcementId, String userId);
    long countByAnnouncementIdAndDeletedAtIsNull(UUID announcementId);
}
