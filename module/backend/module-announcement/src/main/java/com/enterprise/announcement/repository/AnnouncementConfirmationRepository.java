package com.enterprise.announcement.repository;

import com.enterprise.announcement.entity.AnnouncementConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface AnnouncementConfirmationRepository extends JpaRepository<AnnouncementConfirmation, UUID> {
    Optional<AnnouncementConfirmation> findByAnnouncementIdAndUserIdAndDeletedAtIsNull(UUID announcementId, String userId);
    long countByAnnouncementIdAndDeletedAtIsNull(UUID announcementId);
}
