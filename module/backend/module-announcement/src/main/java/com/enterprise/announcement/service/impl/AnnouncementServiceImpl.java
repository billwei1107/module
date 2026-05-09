package com.enterprise.announcement.service.impl;

import com.enterprise.announcement.dto.AnnouncementDTOs.*;
import com.enterprise.announcement.entity.*;
import com.enterprise.announcement.entity.Announcement.AnnouncementStatus;
import com.enterprise.announcement.entity.AnnouncementTarget.TargetType;
import com.enterprise.announcement.repository.*;
import com.enterprise.announcement.service.AnnouncementService;
import com.enterprise.common.annotation.Auditable;
import com.enterprise.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @file AnnouncementServiceImpl.java
 * @description 公告系統服務實作 / Announcement service implementation
 * @description_en Handles scheduled publishing, target filtering, read tracking, and confirmations
 * @description_zh 處理排程發布、發布範圍篩選、已讀追蹤與回條確認
 */
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {
    private final AnnouncementRepository announcementRepository;
    private final AnnouncementTargetRepository targetRepository;
    private final AnnouncementReadRepository readRepository;
    private final AnnouncementConfirmationRepository confirmationRepository;

    @Override
    @Transactional
    @Auditable(module = "announcement", action = "CREATE_ANNOUNCEMENT")
    public AnnouncementDTO createAnnouncement(CreateAnnouncementRequest request) {
        Announcement announcement = new Announcement();
        announcement.setTitle(required(request.getTitle(), "公告標題不可為空 / Announcement title is required"));
        announcement.setContent(required(request.getContent(), "公告內容不可為空 / Announcement content is required"));
        announcement.setCategory(request.getCategory());
        announcement.setAttachmentUrl(request.getAttachmentUrl());
        announcement.setPublisherId(request.getPublisherId());
        announcement.setScheduledPublishAt(request.getScheduledPublishAt() == null ? LocalDateTime.now() : request.getScheduledPublishAt());
        announcement.setScheduledUnpublishAt(request.getScheduledUnpublishAt());
        announcement.setRequiresConfirmation(Boolean.TRUE.equals(request.getRequiresConfirmation()));
        announcement.setStatus(AnnouncementStatus.SCHEDULED);
        Announcement saved = announcementRepository.save(announcement);
        List<AnnouncementTarget> targets = (request.getTargets() == null || request.getTargets().isEmpty() ? List.of(defaultTarget()) : request.getTargets()).stream()
                .map(targetRequest -> createTarget(saved.getId(), targetRequest))
                .map(targetRepository::save)
                .toList();
        return toDTO(saved, targets, null);
    }

    @Override
    public List<AnnouncementDTO> getAnnouncements() {
        return announcementRepository.findByDeletedAtIsNullOrderByCreatedAtDesc().stream().map(announcement -> toDTO(announcement, null)).toList();
    }

    @Override
    @Transactional
    public int publishDueAnnouncements() {
        List<Announcement> due = announcementRepository.findByStatusAndScheduledPublishAtLessThanEqualAndDeletedAtIsNull(AnnouncementStatus.SCHEDULED, LocalDateTime.now());
        due.forEach(announcement -> announcement.setStatus(AnnouncementStatus.PUBLISHED));
        announcementRepository.saveAll(due);
        return due.size();
    }

    @Override
    @Transactional
    public int archiveExpiredAnnouncements() {
        List<Announcement> expired = announcementRepository.findByStatusAndScheduledUnpublishAtLessThanEqualAndDeletedAtIsNull(AnnouncementStatus.PUBLISHED, LocalDateTime.now());
        expired.forEach(announcement -> announcement.setStatus(AnnouncementStatus.ARCHIVED));
        announcementRepository.saveAll(expired);
        return expired.size();
    }

    @Override
    public List<AnnouncementDTO> getVisibleAnnouncements(VisibleAnnouncementQuery query) {
        String userId = required(query.getUserId(), "使用者不可為空 / User is required");
        return announcementRepository.findByStatusAndDeletedAtIsNullOrderByScheduledPublishAtDesc(AnnouncementStatus.PUBLISHED).stream()
                .filter(announcement -> isVisibleTo(announcement.getId(), query))
                .map(announcement -> toDTO(announcement, userId))
                .toList();
    }

    @Override
    @Transactional
    public AnnouncementDTO markRead(String announcementId, String userId) {
        Announcement announcement = findAnnouncement(announcementId);
        String readerId = required(userId, "使用者不可為空 / User is required");
        readRepository.findByAnnouncementIdAndUserIdAndDeletedAtIsNull(announcement.getId(), readerId).orElseGet(() -> {
            AnnouncementRead read = new AnnouncementRead();
            read.setAnnouncementId(announcement.getId());
            read.setUserId(readerId);
            read.setReadAt(LocalDateTime.now());
            return readRepository.save(read);
        });
        return toDTO(announcement, readerId);
    }

    @Override
    @Transactional
    public AnnouncementDTO confirm(String announcementId, String userId) {
        Announcement announcement = findAnnouncement(announcementId);
        if (!Boolean.TRUE.equals(announcement.getRequiresConfirmation())) {
            throw new BusinessException(400, "此公告不需要回條 / Announcement does not require confirmation");
        }
        String confirmerId = required(userId, "使用者不可為空 / User is required");
        markRead(announcementId, confirmerId);
        confirmationRepository.findByAnnouncementIdAndUserIdAndDeletedAtIsNull(announcement.getId(), confirmerId).orElseGet(() -> {
            AnnouncementConfirmation confirmation = new AnnouncementConfirmation();
            confirmation.setAnnouncementId(announcement.getId());
            confirmation.setUserId(confirmerId);
            confirmation.setConfirmedAt(LocalDateTime.now());
            return confirmationRepository.save(confirmation);
        });
        return toDTO(announcement, confirmerId);
    }

    private boolean isVisibleTo(UUID announcementId, VisibleAnnouncementQuery query) {
        List<AnnouncementTarget> targets = targetRepository.findByAnnouncementIdAndDeletedAtIsNullOrderByCreatedAtAsc(announcementId);
        return targets.isEmpty() || targets.stream().anyMatch(target ->
                target.getTargetType() == TargetType.ALL_COMPANY
                        || (target.getTargetType() == TargetType.COMPANY && matches(target.getTargetId(), query.getCompanyId()))
                        || (target.getTargetType() == TargetType.DEPARTMENT && matches(target.getTargetId(), query.getDepartmentId()))
                        || (target.getTargetType() == TargetType.USER && matches(target.getTargetId(), query.getUserId())));
    }

    private boolean matches(String targetId, String value) {
        return targetId != null && value != null && targetId.equals(value);
    }

    private AnnouncementTarget createTarget(UUID announcementId, TargetRequest request) {
        AnnouncementTarget target = new AnnouncementTarget();
        target.setAnnouncementId(announcementId);
        target.setTargetType(request.getTargetType() == null ? TargetType.ALL_COMPANY : request.getTargetType());
        target.setTargetId(request.getTargetId());
        return target;
    }

    private TargetRequest defaultTarget() {
        TargetRequest target = new TargetRequest();
        target.setTargetType(TargetType.ALL_COMPANY);
        return target;
    }

    private Announcement findAnnouncement(String id) {
        return announcementRepository.findById(UUID.fromString(id)).filter(announcement -> announcement.getDeletedAt() == null)
                .orElseThrow(() -> new BusinessException(404, "公告不存在 / Announcement not found"));
    }

    private String required(String value, String message) {
        if (value == null || value.isBlank()) throw new BusinessException(400, message);
        return value.trim();
    }

    private AnnouncementDTO toDTO(Announcement announcement, String userId) {
        return toDTO(announcement, targetRepository.findByAnnouncementIdAndDeletedAtIsNullOrderByCreatedAtAsc(announcement.getId()), userId);
    }

    private AnnouncementDTO toDTO(Announcement announcement, List<AnnouncementTarget> targets, String userId) {
        boolean read = userId != null && readRepository.findByAnnouncementIdAndUserIdAndDeletedAtIsNull(announcement.getId(), userId).isPresent();
        boolean confirmed = userId != null && confirmationRepository.findByAnnouncementIdAndUserIdAndDeletedAtIsNull(announcement.getId(), userId).isPresent();
        return AnnouncementDTO.builder()
                .id(announcement.getId().toString())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .category(announcement.getCategory())
                .attachmentUrl(announcement.getAttachmentUrl())
                .publisherId(announcement.getPublisherId())
                .scheduledPublishAt(announcement.getScheduledPublishAt())
                .scheduledUnpublishAt(announcement.getScheduledUnpublishAt())
                .requiresConfirmation(announcement.getRequiresConfirmation())
                .status(announcement.getStatus())
                .targets((targets == null ? List.<AnnouncementTarget>of() : targets).stream().map(this::toDTO).toList())
                .read(read)
                .confirmed(confirmed)
                .readCount(readRepository.countByAnnouncementIdAndDeletedAtIsNull(announcement.getId()))
                .confirmationCount(confirmationRepository.countByAnnouncementIdAndDeletedAtIsNull(announcement.getId()))
                .build();
    }

    private TargetDTO toDTO(AnnouncementTarget target) {
        return TargetDTO.builder().id(target.getId().toString()).announcementId(target.getAnnouncementId().toString()).targetType(target.getTargetType()).targetId(target.getTargetId()).build();
    }
}
