package com.enterprise.payroll.service;

import com.enterprise.payroll.dto.CreateSalaryItemRequest;
import com.enterprise.payroll.dto.CreateSalaryStructureRequest;
import com.enterprise.payroll.dto.SalaryItemDTO;
import com.enterprise.payroll.dto.SalaryStructureDTO;

import java.util.List;

/**
 * @file PayrollSetupService.java
 * @description 薪資設定服務介面 / Payroll setup service contract
 */
public interface PayrollSetupService {
    SalaryStructureDTO upsertSalaryStructure(CreateSalaryStructureRequest request);

    List<SalaryStructureDTO> getSalaryStructures();

    SalaryItemDTO createSalaryItem(CreateSalaryItemRequest request);

    List<SalaryItemDTO> getSalaryItems();
}
