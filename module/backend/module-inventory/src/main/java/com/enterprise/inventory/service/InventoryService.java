package com.enterprise.inventory.service;

import com.enterprise.inventory.dto.InventoryDTOs.*;
import java.util.List;

/**
 * @file InventoryService.java
 * @description 庫存管理服務介面 / Inventory service contract
 */
public interface InventoryService {
    CategoryDTO createCategory(CreateCategoryRequest request);
    WarehouseDTO createWarehouse(CreateWarehouseRequest request);
    ItemDTO createItem(CreateItemRequest request);
    List<ItemDTO> getItems();
    StockMovementDTO recordMovement(StockMovementRequest request);
    List<StockRecordDTO> getStockRecords();
    StockTakeDTO freezeStockTake(String itemId, String warehouseId);
    StockTakeDTO countStockTake(String stockTakeId, CountStockTakeRequest request);
    StockTakeDTO adjustStockTake(String stockTakeId);
    InventoryReportDTO getInventoryReport();
}
