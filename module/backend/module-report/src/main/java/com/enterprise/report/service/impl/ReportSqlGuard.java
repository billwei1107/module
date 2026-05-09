package com.enterprise.report.service.impl;

import com.enterprise.common.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @file ReportSqlGuard.java
 * @description 報表 SQL 防護 / Report SQL guard
 * @description_en Allows only read-only SELECT statements against approved module table prefixes
 * @description_zh 僅允許針對核准模組表前綴的唯讀 SELECT 查詢
 */
@Component
public class ReportSqlGuard {

    private static final Pattern TABLE_PATTERN = Pattern.compile("\\b(from|join)\\s+([a-zA-Z_][a-zA-Z0-9_]*)", Pattern.CASE_INSENSITIVE);
    private static final List<String> ALLOWED_PREFIXES = List.of(
            "att_", "leave_", "fin_", "pay_", "proj_", "doc_", "org_", "auth_", "sys_", "audit_", "rpt_");
    private static final List<String> BLOCKED_TOKENS = List.of(
            ";", "--", "/*", "*/", " insert ", " update ", " delete ", " drop ", " alter ", " truncate ", " create ", " grant ", " revoke ");

    public String validateSelect(String sql) {
        if (sql == null || sql.isBlank()) {
            throw new BusinessException(400, "報表 SQL 不可為空 / Report SQL is required");
        }
        String normalized = " " + sql.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT) + " ";
        if (!normalized.startsWith(" select ")) {
            throw new BusinessException(400, "報表 SQL 僅允許 SELECT / Only SELECT SQL is allowed");
        }
        for (String token : BLOCKED_TOKENS) {
            if (normalized.contains(token)) {
                throw new BusinessException(400, "報表 SQL 包含禁止語法 / Report SQL contains blocked syntax");
            }
        }
        Matcher matcher = TABLE_PATTERN.matcher(sql);
        boolean foundTable = false;
        while (matcher.find()) {
            foundTable = true;
            String tableName = matcher.group(2).toLowerCase(Locale.ROOT);
            if (ALLOWED_PREFIXES.stream().noneMatch(tableName::startsWith)) {
                throw new BusinessException(400, "報表 SQL 使用未核准資料表 / Report SQL references a non-whitelisted table");
            }
        }
        if (!foundTable) {
            throw new BusinessException(400, "報表 SQL 必須指定資料表 / Report SQL must reference a table");
        }
        return sql.trim();
    }
}
