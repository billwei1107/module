package com.enterprise.meeting.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * @file MeetingRoom.java
 * @description 會議室實體 / Meeting room entity
 * @description_en Stores room capacity, location, and equipment metadata
 * @description_zh 儲存會議室容量、位置與設備資訊
 */
@Entity
@Table(name = "meet_rooms")
@Getter
@Setter
public class MeetingRoom extends BaseEntity {
    @Column(nullable = false, length = 100)
    private String name;
    @Column(length = 100)
    private String location;
    @Column(nullable = false)
    private Integer capacity = 0;
    @Column(length = 500)
    private String equipment;
    @Column(nullable = false)
    private Boolean active = true;
}
