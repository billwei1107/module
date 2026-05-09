package com.enterprise.inventory.service.impl;

import com.enterprise.common.exception.BusinessException;
import com.enterprise.inventory.dto.InventoryDTOs.StockMovementRequest;
import com.enterprise.inventory.entity.Item;
import com.enterprise.inventory.entity.StockMovement.MovementType;
import com.enterprise.inventory.entity.StockRecord;
import com.enterprise.inventory.entity.Warehouse;
import com.enterprise.inventory.repository.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @file InventoryServiceImplTest.java
 * @description 庫存管理服務測試 / Inventory service tests
 */
class InventoryServiceImplTest {

    @Test
    void outboundShouldRejectNegativeStock() {
        UUID itemId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();
        StockRecordRepository recordRepository = mock(StockRecordRepository.class);
        when(recordRepository.findByItemIdAndWarehouseIdAndDeletedAtIsNull(itemId, warehouseId)).thenReturn(Optional.of(record(itemId, warehouseId, "5")));
        InventoryServiceImpl service = service(itemId, warehouseId, recordRepository);

        StockMovementRequest request = new StockMovementRequest();
        request.setItemId(itemId.toString());
        request.setFromWarehouseId(warehouseId.toString());
        request.setType(MovementType.OUTBOUND);
        request.setQuantity(new BigDecimal("6"));

        assertThatThrownBy(() -> service.recordMovement(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("庫存不足");
    }

    @Test
    void transferShouldDecreaseSourceAndIncreaseTarget() {
        UUID itemId = UUID.randomUUID();
        UUID fromWarehouseId = UUID.randomUUID();
        UUID toWarehouseId = UUID.randomUUID();
        StockRecordRepository recordRepository = mock(StockRecordRepository.class);
        StockRecord source = record(itemId, fromWarehouseId, "10");
        StockRecord target = record(itemId, toWarehouseId, "1");
        when(recordRepository.findByItemIdAndWarehouseIdAndDeletedAtIsNull(itemId, fromWarehouseId)).thenReturn(Optional.of(source));
        when(recordRepository.findByItemIdAndWarehouseIdAndDeletedAtIsNull(itemId, toWarehouseId)).thenReturn(Optional.of(target));
        InventoryServiceImpl service = service(itemId, fromWarehouseId, toWarehouseId, recordRepository);

        StockMovementRequest request = new StockMovementRequest();
        request.setItemId(itemId.toString());
        request.setFromWarehouseId(fromWarehouseId.toString());
        request.setToWarehouseId(toWarehouseId.toString());
        request.setType(MovementType.TRANSFER);
        request.setQuantity(new BigDecimal("3"));
        service.recordMovement(request);

        assertThat(source.getQuantity()).isEqualByComparingTo("7");
        assertThat(target.getQuantity()).isEqualByComparingTo("4");
    }

    private InventoryServiceImpl service(UUID itemId, UUID warehouseId, StockRecordRepository recordRepository) {
        return service(itemId, warehouseId, warehouseId, recordRepository);
    }

    private InventoryServiceImpl service(UUID itemId, UUID firstWarehouseId, UUID secondWarehouseId, StockRecordRepository recordRepository) {
        ItemRepository itemRepository = mock(ItemRepository.class);
        WarehouseRepository warehouseRepository = mock(WarehouseRepository.class);
        StockMovementRepository movementRepository = mock(StockMovementRepository.class);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item(itemId)));
        when(warehouseRepository.findById(firstWarehouseId)).thenReturn(Optional.of(warehouse(firstWarehouseId)));
        when(warehouseRepository.findById(secondWarehouseId)).thenReturn(Optional.of(warehouse(secondWarehouseId)));
        when(movementRepository.save(any())).thenAnswer(invocation -> {
            var movement = invocation.getArgument(0, com.enterprise.inventory.entity.StockMovement.class);
            movement.setId(UUID.randomUUID());
            return movement;
        });
        return new InventoryServiceImpl(mock(CategoryRepository.class), itemRepository, warehouseRepository, recordRepository, movementRepository, mock(StockTakeRepository.class));
    }

    private Item item(UUID id) {
        Item item = new Item();
        item.setId(id);
        item.setSku("SKU-001");
        item.setName("測試品項");
        return item;
    }

    private Warehouse warehouse(UUID id) {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(id);
        warehouse.setCode("MAIN");
        warehouse.setName("主倉");
        return warehouse;
    }

    private StockRecord record(UUID itemId, UUID warehouseId, String quantity) {
        StockRecord record = new StockRecord();
        record.setId(UUID.randomUUID());
        record.setItemId(itemId);
        record.setWarehouseId(warehouseId);
        record.setQuantity(new BigDecimal(quantity));
        return record;
    }
}
