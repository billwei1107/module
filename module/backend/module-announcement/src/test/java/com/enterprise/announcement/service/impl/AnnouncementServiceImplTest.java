package com.enterprise.announcement.service.impl;

import com.enterprise.announcement.dto.AnnouncementDTOs.*;
import com.enterprise.announcement.entity.Announcement;
import com.enterprise.announcement.entity.Announcement.AnnouncementStatus;
import com.enterprise.announcement.entity.AnnouncementConfirmation;
import com.enterprise.announcement.entity.AnnouncementRead;
import com.enterprise.announcement.entity.AnnouncementTarget;
import com.enterprise.announcement.entity.AnnouncementTarget.TargetType;
import com.enterprise.announcement.repository.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @file AnnouncementServiceImplTest.java
 * @description 公告系統服務測試 / Announcement service tests
 */
@SuppressWarnings("unchecked")
class AnnouncementServiceImplTest {

    @Test
    void publishDueAnnouncementsShouldPublishScheduledItems() {
        AnnouncementRepository announcementRepository = mock(AnnouncementRepository.class);
        Announcement announcement = announcement("重要公告", AnnouncementStatus.SCHEDULED);
        when(announcementRepository.findByStatusAndScheduledPublishAtLessThanEqualAndDeletedAtIsNull(eq(AnnouncementStatus.SCHEDULED), any(LocalDateTime.class)))
                .thenReturn(List.of(announcement));
        AnnouncementServiceImpl service = service(announcementRepository, mock(AnnouncementTargetRepository.class), mock(AnnouncementReadRepository.class), mock(AnnouncementConfirmationRepository.class));

        int published = service.publishDueAnnouncements();

        assertThat(published).isEqualTo(1);
        assertThat(announcement.getStatus()).isEqualTo(AnnouncementStatus.PUBLISHED);
    }

    @Test
    void visibleAnnouncementsShouldFilterByDepartmentTarget() {
        UUID announcementId = UUID.randomUUID();
        AnnouncementRepository announcementRepository = mock(AnnouncementRepository.class);
        AnnouncementTargetRepository targetRepository = mock(AnnouncementTargetRepository.class);
        Announcement announcement = announcement(announcementId, "部門公告", AnnouncementStatus.PUBLISHED);
        when(announcementRepository.findByStatusAndDeletedAtIsNullOrderByScheduledPublishAtDesc(AnnouncementStatus.PUBLISHED)).thenReturn(List.of(announcement));
        when(targetRepository.findByAnnouncementIdAndDeletedAtIsNullOrderByCreatedAtAsc(announcementId)).thenReturn(List.of(target(announcementId, TargetType.DEPARTMENT, "dept-1")));
        AnnouncementServiceImpl service = service(announcementRepository, targetRepository, mock(AnnouncementReadRepository.class), mock(AnnouncementConfirmationRepository.class));
        VisibleAnnouncementQuery query = new VisibleAnnouncementQuery();
        query.setUserId("user-1");
        query.setDepartmentId("dept-1");

        List<AnnouncementDTO> visible = service.getVisibleAnnouncements(query);

        assertThat(visible).hasSize(1);
        assertThat(visible.getFirst().getTitle()).isEqualTo("部門公告");
    }

    @Test
    void confirmShouldMarkReadAndConfirmedForImportantAnnouncement() {
        UUID announcementId = UUID.randomUUID();
        AnnouncementRepository announcementRepository = mock(AnnouncementRepository.class);
        AnnouncementReadRepository readRepository = mock(AnnouncementReadRepository.class);
        AnnouncementConfirmationRepository confirmationRepository = mock(AnnouncementConfirmationRepository.class);
        Announcement announcement = announcement(announcementId, "重要公告", AnnouncementStatus.PUBLISHED);
        announcement.setRequiresConfirmation(true);
        when(announcementRepository.findById(announcementId)).thenReturn(Optional.of(announcement));
        when(readRepository.findByAnnouncementIdAndUserIdAndDeletedAtIsNull(announcementId, "user-1")).thenReturn(Optional.empty(), Optional.of(new AnnouncementRead()));
        when(confirmationRepository.findByAnnouncementIdAndUserIdAndDeletedAtIsNull(announcementId, "user-1")).thenReturn(Optional.empty(), Optional.empty(), Optional.of(new AnnouncementConfirmation()));
        when(readRepository.save(any(AnnouncementRead.class))).thenAnswer(invocation -> invocation.getArgument(0, AnnouncementRead.class));
        when(confirmationRepository.save(any(AnnouncementConfirmation.class))).thenAnswer(invocation -> invocation.getArgument(0, AnnouncementConfirmation.class));
        AnnouncementServiceImpl service = service(announcementRepository, mock(AnnouncementTargetRepository.class), readRepository, confirmationRepository);

        AnnouncementDTO confirmed = service.confirm(announcementId.toString(), "user-1");

        assertThat(confirmed.isRead()).isTrue();
        assertThat(confirmed.isConfirmed()).isTrue();
        verify(readRepository).save(any(AnnouncementRead.class));
        verify(confirmationRepository).save(any(AnnouncementConfirmation.class));
    }

    private AnnouncementServiceImpl service(AnnouncementRepository announcementRepository, AnnouncementTargetRepository targetRepository, AnnouncementReadRepository readRepository, AnnouncementConfirmationRepository confirmationRepository) {
        when(readRepository.countByAnnouncementIdAndDeletedAtIsNull(any(UUID.class))).thenReturn(0L);
        when(confirmationRepository.countByAnnouncementIdAndDeletedAtIsNull(any(UUID.class))).thenReturn(0L);
        return new AnnouncementServiceImpl(announcementRepository, targetRepository, readRepository, confirmationRepository);
    }

    private Announcement announcement(String title, AnnouncementStatus status) {
        return announcement(UUID.randomUUID(), title, status);
    }

    private Announcement announcement(UUID id, String title, AnnouncementStatus status) {
        Announcement announcement = new Announcement();
        announcement.setId(id);
        announcement.setTitle(title);
        announcement.setContent("內容");
        announcement.setScheduledPublishAt(LocalDateTime.now().minusMinutes(1));
        announcement.setStatus(status);
        announcement.setRequiresConfirmation(false);
        return announcement;
    }

    private AnnouncementTarget target(UUID announcementId, TargetType type, String targetId) {
        AnnouncementTarget target = new AnnouncementTarget();
        target.setId(UUID.randomUUID());
        target.setAnnouncementId(announcementId);
        target.setTargetType(type);
        target.setTargetId(targetId);
        return target;
    }
}
