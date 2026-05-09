package com.enterprise.meeting.repository;

import com.enterprise.meeting.entity.RoomBooking;
import com.enterprise.meeting.entity.RoomBooking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RoomBookingRepository extends JpaRepository<RoomBooking, UUID> {
    List<RoomBooking> findByRoomIdAndStatusAndEndTimeAfterAndStartTimeBeforeAndDeletedAtIsNull(UUID roomId, BookingStatus status, LocalDateTime startTime, LocalDateTime endTime);
    List<RoomBooking> findByDeletedAtIsNullOrderByStartTimeDesc();
}
