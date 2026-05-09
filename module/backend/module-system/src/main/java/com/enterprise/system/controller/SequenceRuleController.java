package com.enterprise.system.controller;

import com.enterprise.common.dto.ApiResponse;
import com.enterprise.system.service.SequenceRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @file SequenceRuleController.java
 * @description 流水號規則控制器 / Sequence rule controller
 */
@RestController
@RequestMapping("/api/v1/system/sequences")
@RequiredArgsConstructor
public class SequenceRuleController {

    private final SequenceRuleService sequenceRuleService;

    @PostMapping("/{name}/next")
    public ApiResponse<String> getNextSequence(@PathVariable String name) {
        return ApiResponse.success(sequenceRuleService.getNextSequence(name));
    }
}
