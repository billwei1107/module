package com.enterprise.payroll.service.impl;

import com.enterprise.common.annotation.Auditable;
import com.enterprise.payroll.dto.CreateSalaryItemRequest;
import com.enterprise.payroll.dto.CreateSalaryStructureRequest;
import com.enterprise.payroll.dto.SalaryItemDTO;
import com.enterprise.payroll.dto.SalaryStructureDTO;
import com.enterprise.payroll.entity.SalaryItem;
import com.enterprise.payroll.entity.SalaryStructure;
import com.enterprise.payroll.repository.SalaryItemRepository;
import com.enterprise.payroll.repository.SalaryStructureRepository;
import com.enterprise.payroll.service.PayrollSetupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @file PayrollSetupServiceImpl.java
 * @description 薪資設定服務實作 / Payroll setup service implementation
 */
@Service
@RequiredArgsConstructor
public class PayrollSetupServiceImpl implements PayrollSetupService {

    private final SalaryStructureRepository salaryStructureRepository;
    private final SalaryItemRepository salaryItemRepository;

    @Override
    @Transactional
    @Auditable(module = "payroll", action = "UPSERT_SALARY_STRUCTURE")
    public SalaryStructureDTO upsertSalaryStructure(CreateSalaryStructureRequest request) {
        SalaryStructure structure = salaryStructureRepository
                .findByEmployeeIdAndActiveTrueAndDeletedAtIsNull(request.getEmployeeId())
                .orElseGet(SalaryStructure::new);
        structure.setName(request.getName());
        structure.setEmployeeId(request.getEmployeeId());
        structure.setType(request.getType());
        structure.setBaseSalary(safeAmount(request.getBaseSalary()));
        structure.setHourlyRate(safeAmount(request.getHourlyRate()));
        structure.setActive(true);
        return toDTO(salaryStructureRepository.save(structure));
    }

    @Override
    public List<SalaryStructureDTO> getSalaryStructures() {
        return salaryStructureRepository.findByDeletedAtIsNullOrderByEmployeeIdAsc().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    @Auditable(module = "payroll", action = "CREATE_SALARY_ITEM")
    public SalaryItemDTO createSalaryItem(CreateSalaryItemRequest request) {
        SalaryItem item = new SalaryItem();
        item.setName(request.getName());
        item.setCode(request.getCode());
        item.setCategory(request.getCategory());
        item.setCalculationType(request.getCalculationType());
        item.setAmount(safeAmount(request.getAmount()));
        item.setPercentage(safeAmount(request.getPercentage()));
        return toDTO(salaryItemRepository.save(item));
    }

    @Override
    public List<SalaryItemDTO> getSalaryItems() {
        return salaryItemRepository.findByActiveTrueAndDeletedAtIsNullOrderByCodeAsc().stream().map(this::toDTO).toList();
    }

    private SalaryStructureDTO toDTO(SalaryStructure structure) {
        return SalaryStructureDTO.builder()
                .id(structure.getId() != null ? structure.getId().toString() : null)
                .name(structure.getName())
                .employeeId(structure.getEmployeeId())
                .type(structure.getType())
                .baseSalary(structure.getBaseSalary())
                .hourlyRate(structure.getHourlyRate())
                .active(structure.getActive())
                .build();
    }

    private SalaryItemDTO toDTO(SalaryItem item) {
        return SalaryItemDTO.builder()
                .id(item.getId() != null ? item.getId().toString() : null)
                .name(item.getName())
                .code(item.getCode())
                .category(item.getCategory())
                .calculationType(item.getCalculationType())
                .amount(item.getAmount())
                .percentage(item.getPercentage())
                .active(item.getActive())
                .build();
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }
}
