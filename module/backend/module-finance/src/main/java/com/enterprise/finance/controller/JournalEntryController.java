package com.enterprise.finance.controller;

import com.enterprise.common.dto.ApiResponse;
import com.enterprise.common.security.SecurityUtils;
import com.enterprise.finance.dto.CreateJournalEntryRequest;
import com.enterprise.finance.dto.JournalEntryDTO;
import com.enterprise.finance.service.JournalEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @file JournalEntryController.java
 * @description 傳票控制器 / Journal entry controller
 */
@RestController
@RequestMapping("/api/v1/finance/journal-entries")
@RequiredArgsConstructor
public class JournalEntryController {

    private final JournalEntryService journalEntryService;

    @GetMapping
    public ApiResponse<List<JournalEntryDTO>> getJournalEntries() {
        return ApiResponse.success(journalEntryService.getJournalEntries());
    }

    @PostMapping
    public ApiResponse<JournalEntryDTO> createJournalEntry(@RequestBody CreateJournalEntryRequest request) {
        return ApiResponse.success(journalEntryService.createJournalEntry(request));
    }

    @PostMapping("/{id}/post")
    public ApiResponse<JournalEntryDTO> postJournalEntry(@PathVariable String id) {
        return ApiResponse.success(journalEntryService.postJournalEntry(id, SecurityUtils.getCurrentUserId()));
    }
}
