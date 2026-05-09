package com.enterprise.inventory.dto;

import com.enterprise.inventory.entity.StockMovement.MovementType;
import com.enterprise.inventory.entity.StockTake.StockTakeStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * @file InventoryDTOs.java
 * @description 庫存管理 DTO 集合 / Inventory DTO collection
 * @description_en Defines inventory request and response data structures
 * @description_zh 定義庫存管理模組請求與回傳資料結構
 */
public final class InventoryDTOs {
    private InventoryDTOs() {}

    @Data public static class CreateCategoryRequest { private String code; private String name; }
    @Data public static class CreateWarehouseRequest { private String code; private String name; private String location; }
    @Data public static class CreateItemRequest { private String categoryId; private String sku; private String name; private String specification; private String barcode; private String unit = "PCS"; private BigDecimal safetyStock = BigDecimal.ZERO; }
    @Data public static class StockMovementRequest { private String itemId; private String fromWarehouseId; private String toWarehouseId; private MovementType type; private BigDecimal quantity = BigDecimal.ZERO; private String referenceNo; private String note; }
    @Data public static class CountStockTakeRequest { private BigDecimal actualQuantity = BigDecimal.ZERO; }

    @Data @Builder public static class CategoryDTO { private String id; private String code; private String name; }
    @Data @Builder public static class WarehouseDTO { private String id; private String code; private String name; private String location; }
    @Data @Builder public static class ItemDTO { private String id; private String categoryId; private String sku; private String name; private String specification; private String barcode; private String unit; private BigDecimal safetyStock; }
    @Data @Builder public static class StockRecordDTO { private String id; private String itemId; private String warehouseId; private BigDecimal quantity; }
    @Data @Builder public static class StockMovementDTO { private String id; private String itemId; private String fromWarehouseId; private String toWarehouseId; private MovementType type; private BigDecimal quantity; private String referenceNo; private String note; }
    @Data @Builder public static class StockTakeDTO { private String id; private String itemId; private String warehouseId; private BigDecimal expectedQuantity; private BigDecimal actualQuantity; private BigDecimal differenceQuantity; private StockTakeStatus status; }
    @Data @Builder public static class InventoryReportDTO { private List<StockRecordDTO> records; private List<StockRecordDTO> lowStockRecords; }
}
