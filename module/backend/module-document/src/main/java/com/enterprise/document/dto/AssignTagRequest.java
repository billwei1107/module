package com.enterprise.document.dto;

import lombok.Data;

/**
 * @file AssignTagRequest.java
 * @description 指派文件標籤請求 / Assign document tag request
 */
@Data
public class AssignTagRequest {
    private String name;
    private String color;
}
