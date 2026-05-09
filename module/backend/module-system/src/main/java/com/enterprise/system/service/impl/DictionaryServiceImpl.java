package com.enterprise.system.service.impl;

import com.enterprise.common.exception.BusinessException;
import com.enterprise.system.dto.CreateDictionaryItemRequest;
import com.enterprise.system.dto.CreateDictionaryRequest;
import com.enterprise.system.dto.DictionaryDTO;
import com.enterprise.system.dto.DictionaryItemDTO;
import com.enterprise.system.entity.Dictionary;
import com.enterprise.system.entity.DictionaryItem;
import com.enterprise.system.repository.DictionaryItemRepository;
import com.enterprise.system.repository.DictionaryRepository;
import com.enterprise.system.service.DictionaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * @file DictionaryServiceImpl.java
 * @description 資料字典服務實作 / Dictionary service implementation
 * @description_zh 管理通用下拉選項群組與項目
 */
@Service
@RequiredArgsConstructor
public class DictionaryServiceImpl implements DictionaryService {

    private final DictionaryRepository dictionaryRepository;
    private final DictionaryItemRepository itemRepository;

    @Override
    public List<DictionaryDTO> getDictionaries() {
        return dictionaryRepository.findByDeletedAtIsNullOrderByCodeAsc()
                .stream().map(this::toDTOWithItems).toList();
    }

    @Override
    @Transactional
    public DictionaryDTO createDictionary(CreateDictionaryRequest request) {
        if (request.getCode() == null || request.getCode().isBlank()) {
            throw new BusinessException(400, "字典代碼不可為空 / Dictionary code is required");
        }
        dictionaryRepository.findByCodeAndDeletedAtIsNull(request.getCode()).ifPresent(existing -> {
            throw new BusinessException(409, "字典代碼已存在 / Dictionary code already exists");
        });

        Dictionary dictionary = new Dictionary();
        dictionary.setCode(request.getCode());
        dictionary.setName(request.getName());
        return toDTOWithItems(dictionaryRepository.save(dictionary));
    }

    @Override
    @Transactional
    public DictionaryItemDTO createItem(String dictionaryId, CreateDictionaryItemRequest request) {
        UUID id = UUID.fromString(dictionaryId);
        dictionaryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "資料字典不存在 / Dictionary not found"));

        DictionaryItem item = new DictionaryItem();
        item.setDictionaryId(id);
        item.setLabel(request.getLabel());
        item.setValue(request.getValue());
        item.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        return toItemDTO(itemRepository.save(item));
    }

    @Override
    public DictionaryDTO getDictionaryByCode(String code) {
        Dictionary dictionary = dictionaryRepository.findByCodeAndDeletedAtIsNull(code)
                .orElseThrow(() -> new BusinessException(404, "資料字典不存在 / Dictionary not found"));
        return toDTOWithItems(dictionary);
    }

    private DictionaryDTO toDTOWithItems(Dictionary dictionary) {
        return DictionaryDTO.builder()
                .id(dictionary.getId() != null ? dictionary.getId().toString() : null)
                .code(dictionary.getCode())
                .name(dictionary.getName())
                .active(dictionary.getActive())
                .items(dictionary.getId() == null ? List.of() : itemRepository
                        .findByDictionaryIdAndDeletedAtIsNullOrderBySortOrderAscLabelAsc(dictionary.getId())
                        .stream().map(this::toItemDTO).toList())
                .build();
    }

    private DictionaryItemDTO toItemDTO(DictionaryItem item) {
        return DictionaryItemDTO.builder()
                .id(item.getId() != null ? item.getId().toString() : null)
                .dictionaryId(item.getDictionaryId().toString())
                .label(item.getLabel())
                .value(item.getValue())
                .sortOrder(item.getSortOrder())
                .active(item.getActive())
                .build();
    }
}
