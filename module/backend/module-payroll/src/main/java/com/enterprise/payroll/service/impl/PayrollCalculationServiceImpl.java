package com.enterprise.payroll.service.impl;

import com.enterprise.common.annotation.Auditable;
import com.enterprise.common.exception.BusinessException;
import com.enterprise.payroll.dto.CreatePayrollAdjustmentRequest;
import com.enterprise.payroll.dto.PayrollDetailDTO;
import com.enterprise.payroll.dto.PayrollRecordDTO;
import com.enterprise.payroll.entity.*;
import com.enterprise.payroll.entity.PayrollRecord.PayrollStatus;
import com.enterprise.payroll.entity.SalaryItem.ItemCategory;
import com.enterprise.payroll.repository.*;
import com.enterprise.payroll.service.PayrollCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * @file PayrollCalculationServiceImpl.java
 * @description 薪資計算服務實作 / Payroll calculation service implementation
 * @description_en Calculates monthly payroll using BigDecimal, salary items, adjustments, insurance, and progressive tax
 * @description_zh 使用 BigDecimal 依薪資項目、調整、勞健保與累進稅率計算月薪
 */
@Service
@RequiredArgsConstructor
public class PayrollCalculationServiceImpl implements PayrollCalculationService {

    private final SalaryStructureRepository salaryStructureRepository;
    private final SalaryItemRepository salaryItemRepository;
    private final PayrollRecordRepository payrollRecordRepository;
    private final PayrollDetailRepository payrollDetailRepository;
    private final TaxConfigRepository taxConfigRepository;
    private final InsuranceConfigRepository insuranceConfigRepository;
    private final PayrollAdjustmentRepository payrollAdjustmentRepository;

    @Override
    @Transactional
    @Auditable(module = "payroll", action = "CALCULATE_MONTHLY_PAYROLL")
    public PayrollRecordDTO calculateMonthly(String employeeId, String yearMonth) {
        SalaryStructure structure = salaryStructureRepository.findByEmployeeIdAndActiveTrueAndDeletedAtIsNull(employeeId)
                .orElseThrow(() -> new BusinessException(404, "薪資結構不存在 / Salary structure not found"));
        PayrollRecord record = payrollRecordRepository.findByEmployeeIdAndYearMonthAndDeletedAtIsNull(employeeId, yearMonth)
                .orElseGet(PayrollRecord::new);
        if (record.getId() != null) {
            payrollDetailRepository.deleteByPayrollRecordId(record.getId());
        }

        BigDecimal baseSalary = safeAmount(structure.getBaseSalary());
        List<PayrollDetail> details = new ArrayList<>();
        details.add(detail(null, "BASE", "底薪", "EARNING", baseSalary, "Base salary"));

        for (SalaryItem item : salaryItemRepository.findByActiveTrueAndDeletedAtIsNullOrderByCodeAsc()) {
            BigDecimal amount = calculateItemAmount(baseSalary, item);
            if (amount.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            details.add(detail(item.getId(), item.getCode(), item.getName(), item.getCategory().name(), amount, item.getCalculationType().name()));
        }

        for (PayrollAdjustment adjustment : payrollAdjustmentRepository.findByEmployeeIdAndYearMonthAndDeletedAtIsNull(employeeId, yearMonth)) {
            String category = adjustment.getAmount().compareTo(BigDecimal.ZERO) >= 0 ? "EARNING" : "DEDUCTION";
            details.add(detail(null, adjustment.getAdjustmentType(), adjustment.getAdjustmentType(), category,
                    adjustment.getAmount().abs(), adjustment.getDescription()));
        }

        BigDecimal grossEarnings = sumByCategory(details, "EARNING");
        BigDecimal itemDeductions = sumByCategory(details, "DEDUCTION");
        BigDecimal insurance = calculateEmployeeInsurance(baseSalary);
        BigDecimal taxableIncome = grossEarnings.subtract(itemDeductions).subtract(insurance);
        BigDecimal tax = calculateTax(taxableIncome.max(BigDecimal.ZERO));

        details.add(detail(null, "INSURANCE", "勞健保", "DEDUCTION", insurance, "Employee insurance"));
        details.add(detail(null, "TAX", "所得稅", "DEDUCTION", tax, "Progressive income tax"));

        BigDecimal totalEarnings = sumByCategory(details, "EARNING");
        BigDecimal totalDeductions = sumByCategory(details, "DEDUCTION");
        record.setEmployeeId(employeeId);
        record.setYearMonth(yearMonth);
        record.setBaseSalary(baseSalary);
        record.setTotalEarnings(totalEarnings);
        record.setTotalDeductions(totalDeductions);
        record.setNetPay(totalEarnings.subtract(totalDeductions));
        record.setStatus(PayrollStatus.DRAFT);
        PayrollRecord savedRecord = payrollRecordRepository.save(record);

        details.forEach(detail -> detail.setPayrollRecordId(savedRecord.getId()));
        payrollDetailRepository.saveAll(details);
        return toDTO(savedRecord, details);
    }

    @Override
    public List<PayrollRecordDTO> getPayrollRecords(String employeeId, String yearMonth) {
        List<PayrollRecord> records;
        if (employeeId != null && !employeeId.isBlank()) {
            records = payrollRecordRepository.findByEmployeeIdAndDeletedAtIsNullOrderByYearMonthDesc(employeeId);
        } else if (yearMonth != null && !yearMonth.isBlank()) {
            records = payrollRecordRepository.findByYearMonthAndDeletedAtIsNullOrderByEmployeeIdAsc(yearMonth);
        } else {
            records = payrollRecordRepository.findByDeletedAtIsNullOrderByYearMonthDescEmployeeIdAsc();
        }
        return records.stream()
                .map(record -> toDTO(record, payrollDetailRepository.findByPayrollRecordIdAndDeletedAtIsNull(record.getId())))
                .toList();
    }

    @Override
    @Transactional
    @Auditable(module = "payroll", action = "CONFIRM_PAYROLL_RECORD")
    public PayrollRecordDTO confirmPayrollRecord(String id, String confirmedBy) {
        PayrollRecord record = payrollRecordRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new BusinessException(404, "薪資紀錄不存在 / Payroll record not found"));
        record.setStatus(PayrollStatus.CONFIRMED);
        record.setConfirmedBy(confirmedBy);
        PayrollRecord savedRecord = payrollRecordRepository.save(record);
        return toDTO(savedRecord, payrollDetailRepository.findByPayrollRecordIdAndDeletedAtIsNull(savedRecord.getId()));
    }

    @Override
    @Transactional
    public void createAdjustment(CreatePayrollAdjustmentRequest request) {
        PayrollAdjustment adjustment = new PayrollAdjustment();
        adjustment.setEmployeeId(request.getEmployeeId());
        adjustment.setYearMonth(request.getYearMonth());
        adjustment.setAdjustmentType(request.getAdjustmentType());
        adjustment.setAmount(safeAmount(request.getAmount()));
        adjustment.setDescription(request.getDescription());
        payrollAdjustmentRepository.save(adjustment);
    }

    @Override
    public BigDecimal calculateTax(BigDecimal taxableIncome) {
        BigDecimal income = safeAmount(taxableIncome);
        return taxConfigRepository.findByDeletedAtIsNullOrderByBracketStartAsc().stream()
                .filter(config -> income.compareTo(config.getBracketStart()) >= 0)
                .filter(config -> config.getBracketEnd() == null || income.compareTo(config.getBracketEnd()) <= 0)
                .max(Comparator.comparing(TaxConfig::getBracketStart))
                .map(config -> income.multiply(config.getRate()).subtract(config.getDeduction()).max(BigDecimal.ZERO))
                .orElse(BigDecimal.ZERO)
                .setScale(4, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateEmployeeInsurance(BigDecimal salaryBase) {
        BigDecimal base = safeAmount(salaryBase);
        return insuranceConfigRepository.findByDeletedAtIsNullOrderByTypeAsc().stream()
                .map(config -> base.min(config.getCeiling()).multiply(config.getEmployeeRate()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateItemAmount(BigDecimal baseSalary, SalaryItem item) {
        if (item.getCalculationType() == SalaryItem.CalculationType.PERCENTAGE) {
            return safeAmount(baseSalary).multiply(safeAmount(item.getPercentage())).setScale(4, RoundingMode.HALF_UP);
        }
        return safeAmount(item.getAmount());
    }

    private BigDecimal sumByCategory(List<PayrollDetail> details, String category) {
        return details.stream()
                .filter(detail -> category.equals(detail.getCategory()))
                .map(PayrollDetail::getAmount)
                .map(this::safeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private PayrollDetail detail(UUID itemId, String code, String name, String category, BigDecimal amount, String description) {
        PayrollDetail detail = new PayrollDetail();
        detail.setSalaryItemId(itemId);
        detail.setItemCode(code);
        detail.setItemName(name);
        detail.setCategory(category);
        detail.setAmount(safeAmount(amount));
        detail.setDescription(description);
        return detail;
    }

    private PayrollRecordDTO toDTO(PayrollRecord record, List<PayrollDetail> details) {
        return PayrollRecordDTO.builder()
                .id(record.getId() != null ? record.getId().toString() : null)
                .employeeId(record.getEmployeeId())
                .yearMonth(record.getYearMonth())
                .baseSalary(record.getBaseSalary())
                .totalEarnings(record.getTotalEarnings())
                .totalDeductions(record.getTotalDeductions())
                .netPay(record.getNetPay())
                .status(record.getStatus())
                .details(details.stream().map(this::toDTO).toList())
                .build();
    }

    private PayrollDetailDTO toDTO(PayrollDetail detail) {
        return PayrollDetailDTO.builder()
                .id(detail.getId() != null ? detail.getId().toString() : null)
                .itemCode(detail.getItemCode())
                .itemName(detail.getItemName())
                .category(detail.getCategory())
                .amount(detail.getAmount())
                .description(detail.getDescription())
                .build();
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }
}
