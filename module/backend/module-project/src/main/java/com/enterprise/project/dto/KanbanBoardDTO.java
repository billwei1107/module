package com.enterprise.project.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @file KanbanBoardDTO.java
 * @description 看板資料回傳 / Kanban board response DTO
 */
@Data
@Builder
public class KanbanBoardDTO {
    private List<TaskDTO> todo;
    private List<TaskDTO> inProgress;
    private List<TaskDTO> done;
}
