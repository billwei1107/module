package com.enterprise.system.repository;

import com.enterprise.system.entity.Dictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @file DictionaryRepository.java
 * @description 資料字典資料存取 / Dictionary repository
 */
@Repository
public interface DictionaryRepository extends JpaRepository<Dictionary, UUID> {
    Optional<Dictionary> findByCodeAndDeletedAtIsNull(String code);

    List<Dictionary> findByDeletedAtIsNullOrderByCodeAsc();
}
