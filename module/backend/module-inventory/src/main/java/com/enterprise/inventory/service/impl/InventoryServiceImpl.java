package com.enterprise.inventory.service.impl;

import com.enterprise.common.annotation.Auditable;
import com.enterprise.common.exception.BusinessException;
import com.enterprise.inventory.dto.InventoryDTOs.*;
import com.enterprise.inventory.entity.*;
import com.enterprise.inventory.entity.StockMovement.MovementType;
import com.enterprise.inventory.entity.StockTake.StockTakeStatus;
import com.enterprise.inventory.repository.*;
import com.enterprise.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * @file InventoryServiceImpl.java
 * @description 庫存管理服務實作 / Inventory service implementation
 * @description_en Handles item setup, stock movements, stock takes, and low-stock reports
 * @description_zh 處理品項設定、庫存異動、盤點與低庫存報表
 */
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockRecordRepository stockRecordRepository;
    private final StockMovementRepository movementRepository;
    private final StockTakeRepository stockTakeRepository;

    @Override
    @Transactional
    public CategoryDTO createCategory(CreateCategoryRequest request) {
        Category category = new Category();
        category.setCode(request.getCode());
        category.setName(required(request.getName(), "分類名稱不可為空 / Category name is required"));
        return toDTO(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public WarehouseDTO createWarehouse(CreateWarehouseRequest request) {
        Warehouse warehouse = new Warehouse();
        warehouse.setCode(required(request.getCode(), "倉庫代碼不可為空 / Warehouse code is required"));
        warehouse.setName(required(request.getName(), "倉庫名稱不可為空 / Warehouse name is required"));
        warehouse.setLocation(request.getLocation());
        return toDTO(warehouseRepository.save(warehouse));
    }

    @Override
    @Transactional
    public ItemDTO createItem(CreateItemRequest request) {
        Item item = new Item();
        item.setCategoryId(parseUuid(request.getCategoryId()));
        item.setSku(required(request.getSku(), "SKU 不可為空 / SKU is required"));
        item.setName(required(request.getName(), "品項名稱不可為空 / Item name is required"));
        item.setSpecification(request.getSpecification());
        item.setBarcode(request.getBarcode());
        item.setUnit(request.getUnit() == null || request.getUnit().isBlank() ? "PCS" : request.getUnit());
        item.setSafetyStock(request.getSafetyStock() == null ? BigDecimal.ZERO : request.getSafetyStock());
        return toDTO(itemRepository.save(item));
    }

    @Override
    public List<ItemDTO> getItems() {
        return itemRepository.findByDeletedAtIsNullOrderByNameAsc().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    @Auditable(module = "inventory", action = "RECORD_STOCK_MOVEMENT")
    public StockMovementDTO recordMovement(StockMovementRequest request) {
        MovementType type = request.getType() == null ? MovementType.INBOUND : request.getType();
        BigDecimal quantity = positive(request.getQuantity());
        UUID itemId = findItem(request.getItemId()).getId();
        UUID fromWarehouseId = parseUuid(request.getFromWarehouseId());
        UUID toWarehouseId = parseUuid(request.getToWarehouseId());
        if (type == MovementType.INBOUND) {
            increase(itemId, requireWarehouse(toWarehouseId), quantity);
        } else if (type == MovementType.OUTBOUND) {
            decrease(itemId, requireWarehouse(fromWarehouseId), quantity);
        } else if (type == MovementType.TRANSFER) {
            decrease(itemId, requireWarehouse(fromWarehouseId), quantity);
            increase(itemId, requireWarehouse(toWarehouseId), quantity);
        } else {
            increase(itemId, requireWarehouse(toWarehouseId), quantity);
        }
        StockMovement movement = new StockMovement();
        movement.setItemId(itemId);
        movement.setFromWarehouseId(fromWarehouseId);
        movement.setToWarehouseId(toWarehouseId);
        movement.setType(type);
        movement.setQuantity(quantity);
        movement.setReferenceNo(request.getReferenceNo());
        movement.setNote(request.getNote());
        return toDTO(movementRepository.save(movement));
    }

    @Override
    public List<StockRecordDTO> getStockRecords() {
        return stockRecordRepository.findByDeletedAtIsNullOrderByCreatedAtDesc().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    public StockTakeDTO freezeStockTake(String itemId, String warehouseId) {
        UUID parsedItemId = findItem(itemId).getId();
        UUID parsedWarehouseId = requireWarehouse(UUID.fromString(warehouseId));
        StockRecord record = findOrCreateRecord(parsedItemId, parsedWarehouseId);
        StockTake stockTake = new StockTake();
        stockTake.setItemId(parsedItemId);
        stockTake.setWarehouseId(parsedWarehouseId);
        stockTake.setExpectedQuantity(record.getQuantity());
        return toDTO(stockTakeRepository.save(stockTake));
    }

    @Override
    @Transactional
    public StockTakeDTO countStockTake(String stockTakeId, CountStockTakeRequest request) {
        StockTake stockTake = findStockTake(stockTakeId);
        stockTake.setActualQuantity(request.getActualQuantity() == null ? BigDecimal.ZERO : request.getActualQuantity());
        stockTake.setDifferenceQuantity(stockTake.getActualQuantity().subtract(stockTake.getExpectedQuantity()));
        stockTake.setStatus(StockTakeStatus.COUNTED);
        return toDTO(stockTakeRepository.save(stockTake));
    }

    @Override
    @Transactional
    public StockTakeDTO adjustStockTake(String stockTakeId) {
        StockTake stockTake = findStockTake(stockTakeId);
        if (stockTake.getStatus() != StockTakeStatus.COUNTED) {
            throw new BusinessException(400, "盤點尚未完成，不可調整 / Stock take must be counted before adjustment");
        }
        StockRecord record = findOrCreateRecord(stockTake.getItemId(), stockTake.getWarehouseId());
        record.setQuantity(stockTake.getActualQuantity());
        stockRecordRepository.save(record);
        stockTake.setStatus(StockTakeStatus.ADJUSTED);
        return toDTO(stockTakeRepository.save(stockTake));
    }

    @Override
    public InventoryReportDTO getInventoryReport() {
        List<StockRecordDTO> records = getStockRecords();
        List<StockRecordDTO> lowStock = stockRecordRepository.findByDeletedAtIsNullOrderByCreatedAtDesc().stream()
                .filter(record -> {
                    Item item = itemRepository.findById(record.getItemId()).orElse(null);
                    return item != null && record.getQuantity().compareTo(item.getSafetyStock()) < 0;
                })
                .map(this::toDTO)
                .toList();
        return InventoryReportDTO.builder().records(records).lowStockRecords(lowStock).build();
    }

    private void increase(UUID itemId, UUID warehouseId, BigDecimal quantity) {
        StockRecord record = findOrCreateRecord(itemId, warehouseId);
        record.setQuantity(record.getQuantity().add(quantity));
        stockRecordRepository.save(record);
    }

    private void decrease(UUID itemId, UUID warehouseId, BigDecimal quantity) {
        StockRecord record = findOrCreateRecord(itemId, warehouseId);
        BigDecimal nextQuantity = record.getQuantity().subtract(quantity);
        if (nextQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(400, "庫存不足，不可出現負庫存 / Insufficient stock");
        }
        record.setQuantity(nextQuantity);
        stockRecordRepository.save(record);
    }

    private StockRecord findOrCreateRecord(UUID itemId, UUID warehouseId) {
        return stockRecordRepository.findByItemIdAndWarehouseIdAndDeletedAtIsNull(itemId, warehouseId)
                .orElseGet(() -> {
                    StockRecord record = new StockRecord();
                    record.setItemId(itemId);
                    record.setWarehouseId(warehouseId);
                    record.setQuantity(BigDecimal.ZERO);
                    return record;
                });
    }

    private Item findItem(String itemId) {
        return itemRepository.findById(UUID.fromString(itemId)).filter(item -> item.getDeletedAt() == null)
                .orElseThrow(() -> new BusinessException(404, "品項不存在 / Item not found"));
    }

    private UUID requireWarehouse(UUID warehouseId) {
        if (warehouseId == null) throw new BusinessException(400, "倉庫不可為空 / Warehouse is required");
        warehouseRepository.findById(warehouseId).filter(warehouse -> warehouse.getDeletedAt() == null)
                .orElseThrow(() -> new BusinessException(404, "倉庫不存在 / Warehouse not found"));
        return warehouseId;
    }

    private StockTake findStockTake(String stockTakeId) {
        return stockTakeRepository.findById(UUID.fromString(stockTakeId)).filter(stockTake -> stockTake.getDeletedAt() == null)
                .orElseThrow(() -> new BusinessException(404, "盤點不存在 / Stock take not found"));
    }

    private BigDecimal positive(BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "數量必須大於 0 / Quantity must be positive");
        }
        return quantity;
    }

    private UUID parseUuid(String value) { return value == null || value.isBlank() ? null : UUID.fromString(value); }
    private String required(String value, String message) {
        if (value == null || value.isBlank()) throw new BusinessException(400, message);
        return value.trim();
    }

    private CategoryDTO toDTO(Category c) { return CategoryDTO.builder().id(c.getId().toString()).code(c.getCode()).name(c.getName()).build(); }
    private WarehouseDTO toDTO(Warehouse w) { return WarehouseDTO.builder().id(w.getId().toString()).code(w.getCode()).name(w.getName()).location(w.getLocation()).build(); }
    private ItemDTO toDTO(Item i) { return ItemDTO.builder().id(i.getId().toString()).categoryId(i.getCategoryId() == null ? null : i.getCategoryId().toString()).sku(i.getSku()).name(i.getName()).specification(i.getSpecification()).barcode(i.getBarcode()).unit(i.getUnit()).safetyStock(i.getSafetyStock()).build(); }
    private StockRecordDTO toDTO(StockRecord r) { return StockRecordDTO.builder().id(r.getId() == null ? null : r.getId().toString()).itemId(r.getItemId().toString()).warehouseId(r.getWarehouseId().toString()).quantity(r.getQuantity()).build(); }
    private StockMovementDTO toDTO(StockMovement m) { return StockMovementDTO.builder().id(m.getId().toString()).itemId(m.getItemId().toString()).fromWarehouseId(m.getFromWarehouseId() == null ? null : m.getFromWarehouseId().toString()).toWarehouseId(m.getToWarehouseId() == null ? null : m.getToWarehouseId().toString()).type(m.getType()).quantity(m.getQuantity()).referenceNo(m.getReferenceNo()).note(m.getNote()).build(); }
    private StockTakeDTO toDTO(StockTake s) { return StockTakeDTO.builder().id(s.getId().toString()).itemId(s.getItemId().toString()).warehouseId(s.getWarehouseId().toString()).expectedQuantity(s.getExpectedQuantity()).actualQuantity(s.getActualQuantity()).differenceQuantity(s.getDifferenceQuantity()).status(s.getStatus()).build(); }
}
