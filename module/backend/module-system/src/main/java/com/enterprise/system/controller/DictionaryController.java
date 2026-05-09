package com.enterprise.system.controller;

import com.enterprise.common.dto.ApiResponse;
import com.enterprise.system.dto.CreateDictionaryItemRequest;
import com.enterprise.system.dto.CreateDictionaryRequest;
import com.enterprise.system.dto.DictionaryDTO;
import com.enterprise.system.dto.DictionaryItemDTO;
import com.enterprise.system.service.DictionaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @file DictionaryController.java
 * @description 資料字典控制器 / Dictionary controller
 */
@RestController
@RequestMapping("/api/v1/system/dictionaries")
@RequiredArgsConstructor
public class DictionaryController {

    private final DictionaryService dictionaryService;

    @GetMapping
    public ApiResponse<List<DictionaryDTO>> getDictionaries() {
        return ApiResponse.success(dictionaryService.getDictionaries());
    }

    @GetMapping("/{code}")
    public ApiResponse<DictionaryDTO> getDictionaryByCode(@PathVariable String code) {
        return ApiResponse.success(dictionaryService.getDictionaryByCode(code));
    }

    @PostMapping
    public ApiResponse<DictionaryDTO> createDictionary(@RequestBody CreateDictionaryRequest request) {
        return ApiResponse.success(dictionaryService.createDictionary(request));
    }

    @PostMapping("/{dictionaryId}/items")
    public ApiResponse<DictionaryItemDTO> createItem(
            @PathVariable String dictionaryId,
            @RequestBody CreateDictionaryItemRequest request) {
        return ApiResponse.success(dictionaryService.createItem(dictionaryId, request));
    }
}
