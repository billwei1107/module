package com.enterprise.project.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @file GanttDataDTO.java
 * @description 甘特圖資料回傳 / Gantt data response DTO
 */
@Data
@Builder
public class GanttDataDTO {
    private List<TaskDTO> tasks;
    private List<MilestoneDTO> milestones;
}
