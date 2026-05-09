package com.enterprise.payroll.service.impl;

import com.enterprise.payroll.dto.CreatePayrollAdjustmentRequest;
import com.enterprise.payroll.dto.PayrollRecordDTO;
import com.enterprise.payroll.entity.*;
import com.enterprise.payroll.repository.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @file PayrollCalculationServiceImplTest.java
 * @description 薪資計算服務測試 / Payroll calculation service tests
 */
class PayrollCalculationServiceImplTest {

    @Test
    void calculateTaxShouldUseProgressiveBracketWithBigDecimalPrecision() {
        PayrollCalculationServiceImpl service = serviceWithTaxAndInsurance();

        BigDecimal tax = service.calculateTax(new BigDecimal("80000.00"));

        assertThat(tax).isEqualByComparingTo(new BigDecimal("6100.0000"));
    }

    @Test
    void calculateEmployeeInsuranceShouldApplyCeilingAndRates() {
        PayrollCalculationServiceImpl service = serviceWithTaxAndInsurance();

        BigDecimal insurance = service.calculateEmployeeInsurance(new BigDecimal("60000.00"));

        assertThat(insurance).isEqualByComparingTo(new BigDecimal("1816.0000"));
    }

    @Test
    void calculateMonthlyShouldCreatePayrollRecordWithDetails() {
        SalaryStructureRepository structureRepository = mock(SalaryStructureRepository.class);
        SalaryItemRepository itemRepository = mock(SalaryItemRepository.class);
        PayrollRecordRepository recordRepository = mock(PayrollRecordRepository.class);
        PayrollDetailRepository detailRepository = mock(PayrollDetailRepository.class);
        TaxConfigRepository taxRepository = taxRepository();
        InsuranceConfigRepository insuranceRepository = insuranceRepository();
        PayrollAdjustmentRepository adjustmentRepository = mock(PayrollAdjustmentRepository.class);

        SalaryStructure structure = new SalaryStructure();
        structure.setEmployeeId("emp-001");
        structure.setName("一般月薪");
        structure.setBaseSalary(new BigDecimal("60000.00"));
        when(structureRepository.findByEmployeeIdAndActiveTrueAndDeletedAtIsNull("emp-001")).thenReturn(Optional.of(structure));

        SalaryItem allowance = new SalaryItem();
        allowance.setId(UUID.randomUUID());
        allowance.setCode("MEAL");
        allowance.setName("餐費津貼");
        allowance.setCategory(SalaryItem.ItemCategory.EARNING);
        allowance.setCalculationType(SalaryItem.CalculationType.FIXED);
        allowance.setAmount(new BigDecimal("2000.00"));
        when(itemRepository.findByActiveTrueAndDeletedAtIsNullOrderByCodeAsc()).thenReturn(List.of(allowance));

        PayrollAdjustment overtime = new PayrollAdjustment();
        overtime.setAdjustmentType("OVERTIME");
        overtime.setAmount(new BigDecimal("1000.00"));
        when(adjustmentRepository.findByEmployeeIdAndYearMonthAndDeletedAtIsNull("emp-001", "2026-05")).thenReturn(List.of(overtime));
        when(recordRepository.findByEmployeeIdAndYearMonthAndDeletedAtIsNull("emp-001", "2026-05")).thenReturn(Optional.empty());
        when(recordRepository.save(any())).thenAnswer(invocation -> {
            PayrollRecord record = invocation.getArgument(0);
            record.setId(UUID.randomUUID());
            return record;
        });
        when(detailRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PayrollCalculationServiceImpl service = new PayrollCalculationServiceImpl(
                structureRepository, itemRepository, recordRepository, detailRepository,
                taxRepository, insuranceRepository, adjustmentRepository);

        PayrollRecordDTO result = service.calculateMonthly("emp-001", "2026-05");

        assertThat(result.getTotalEarnings()).isEqualByComparingTo(new BigDecimal("63000.00"));
        assertThat(result.getTotalDeductions()).isGreaterThan(BigDecimal.ZERO);
        assertThat(result.getNetPay()).isEqualByComparingTo(result.getTotalEarnings().subtract(result.getTotalDeductions()));
        assertThat(result.getDetails()).extracting("itemCode").contains("BASE", "MEAL", "OVERTIME", "INSURANCE", "TAX");
    }

    private PayrollCalculationServiceImpl serviceWithTaxAndInsurance() {
        return new PayrollCalculationServiceImpl(
                mock(SalaryStructureRepository.class),
                mock(SalaryItemRepository.class),
                mock(PayrollRecordRepository.class),
                mock(PayrollDetailRepository.class),
                taxRepository(),
                insuranceRepository(),
                mock(PayrollAdjustmentRepository.class));
    }

    private TaxConfigRepository taxRepository() {
        TaxConfigRepository repository = mock(TaxConfigRepository.class);
        TaxConfig low = tax("0", "50000", "0.05", "0");
        TaxConfig mid = tax("50000", "100000", "0.12", "3500");
        when(repository.findByDeletedAtIsNullOrderByBracketStartAsc()).thenReturn(List.of(low, mid));
        return repository;
    }

    private InsuranceConfigRepository insuranceRepository() {
        InsuranceConfigRepository repository = mock(InsuranceConfigRepository.class);
        InsuranceConfig labor = insurance(InsuranceConfig.InsuranceType.LABOR, "0.02", "45800");
        InsuranceConfig health = insurance(InsuranceConfig.InsuranceType.HEALTH, "0.015", "200000");
        when(repository.findByDeletedAtIsNullOrderByTypeAsc()).thenReturn(List.of(labor, health));
        return repository;
    }

    private TaxConfig tax(String start, String end, String rate, String deduction) {
        TaxConfig config = new TaxConfig();
        config.setBracketStart(new BigDecimal(start));
        config.setBracketEnd(new BigDecimal(end));
        config.setRate(new BigDecimal(rate));
        config.setDeduction(new BigDecimal(deduction));
        return config;
    }

    private InsuranceConfig insurance(InsuranceConfig.InsuranceType type, String rate, String ceiling) {
        InsuranceConfig config = new InsuranceConfig();
        config.setType(type);
        config.setEmployeeRate(new BigDecimal(rate));
        config.setCeiling(new BigDecimal(ceiling));
        return config;
    }
}
