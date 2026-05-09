package com.enterprise.inventory.controller;

import com.enterprise.common.dto.ApiResponse;
import com.enterprise.inventory.dto.InventoryDTOs.*;
import com.enterprise.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @file InventoryController.java
 * @description 庫存管理控制器 / Inventory controller
 */
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @PostMapping("/categories") public ApiResponse<CategoryDTO> createCategory(@RequestBody CreateCategoryRequest request) { return ApiResponse.success(inventoryService.createCategory(request)); }
    @PostMapping("/warehouses") public ApiResponse<WarehouseDTO> createWarehouse(@RequestBody CreateWarehouseRequest request) { return ApiResponse.success(inventoryService.createWarehouse(request)); }
    @GetMapping("/items") public ApiResponse<List<ItemDTO>> getItems() { return ApiResponse.success(inventoryService.getItems()); }
    @PostMapping("/items") public ApiResponse<ItemDTO> createItem(@RequestBody CreateItemRequest request) { return ApiResponse.success(inventoryService.createItem(request)); }
    @PostMapping("/movements") public ApiResponse<StockMovementDTO> recordMovement(@RequestBody StockMovementRequest request) { return ApiResponse.success(inventoryService.recordMovement(request)); }
    @GetMapping("/records") public ApiResponse<List<StockRecordDTO>> getStockRecords() { return ApiResponse.success(inventoryService.getStockRecords()); }
    @PostMapping("/stock-takes/freeze") public ApiResponse<StockTakeDTO> freezeStockTake(@RequestParam String itemId, @RequestParam String warehouseId) { return ApiResponse.success(inventoryService.freezeStockTake(itemId, warehouseId)); }
    @PostMapping("/stock-takes/{id}/count") public ApiResponse<StockTakeDTO> countStockTake(@PathVariable String id, @RequestBody CountStockTakeRequest request) { return ApiResponse.success(inventoryService.countStockTake(id, request)); }
    @PostMapping("/stock-takes/{id}/adjust") public ApiResponse<StockTakeDTO> adjustStockTake(@PathVariable String id) { return ApiResponse.success(inventoryService.adjustStockTake(id)); }
    @GetMapping("/report") public ApiResponse<InventoryReportDTO> getInventoryReport() { return ApiResponse.success(inventoryService.getInventoryReport()); }
}
