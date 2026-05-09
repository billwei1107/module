package com.enterprise.system.service;

import com.enterprise.system.dto.CreateDictionaryItemRequest;
import com.enterprise.system.dto.CreateDictionaryRequest;
import com.enterprise.system.dto.DictionaryDTO;
import com.enterprise.system.dto.DictionaryItemDTO;

import java.util.List;

/**
 * @file DictionaryService.java
 * @description 資料字典服務介面 / Dictionary service interface
 */
public interface DictionaryService {
    List<DictionaryDTO> getDictionaries();

    DictionaryDTO createDictionary(CreateDictionaryRequest request);

    DictionaryItemDTO createItem(String dictionaryId, CreateDictionaryItemRequest request);

    DictionaryDTO getDictionaryByCode(String code);
}
