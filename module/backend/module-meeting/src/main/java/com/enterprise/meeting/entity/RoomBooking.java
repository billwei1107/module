package com.enterprise.meeting.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @file RoomBooking.java
 * @description 會議室預約實體 / Room booking entity
 * @description_en Stores room reservation time windows and booking status
 * @description_zh 儲存會議室預約時段與預約狀態
 */
@Entity
@Table(name = "meet_bookings")
@Getter
@Setter
public class RoomBooking extends BaseEntity {
    public enum BookingStatus { BOOKED, CANCELLED }

    @Column(nullable = false)
    private UUID roomId;
    @Column(nullable = false, length = 160)
    private String title;
    @Column(length = 64)
    private String organizerId;
    @Column(nullable = false)
    private LocalDateTime startTime;
    @Column(nullable = false)
    private LocalDateTime endTime;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status = BookingStatus.BOOKED;
}
