package com.enterprise.meeting.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

/**
 * @file MeetingAttendee.java
 * @description 會議與會者實體 / Meeting attendee entity
 * @description_en Stores invited attendees and response status
 * @description_zh 儲存會議邀請對象與回覆狀態
 */
@Entity
@Table(name = "meet_attendees")
@Getter
@Setter
public class MeetingAttendee extends BaseEntity {
    public enum AttendeeResponse { INVITED, ACCEPTED, DECLINED }

    @Column(nullable = false)
    private UUID meetingId;
    @Column(nullable = false, length = 64)
    private String attendeeId;
    @Column(length = 100)
    private String attendeeName;
    @Column(length = 160)
    private String email;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendeeResponse response = AttendeeResponse.INVITED;
}
