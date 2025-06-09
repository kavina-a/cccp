package com.syos.application.dto.builder;

import com.syos.domain.entity.WebInventory;

import java.time.LocalDateTime;

public class WebInventoryBuilder {
    private String itemCode;
    private String itemName;
//    private BigDecimal price;
    private int quantityTransferred;
    private LocalDateTime updatedDateTime;
    private Integer lastUpdatedBy;

    public WebInventoryBuilder withItemCode(String itemCode) {
        this.itemCode = itemCode;
        return this;
    }

    public WebInventoryBuilder withItemName(String itemName) {
        this.itemName = itemName;
        return this;
    }

    public WebInventoryBuilder withQuantityTransferred(int quantityTransferred) {
        this.quantityTransferred = quantityTransferred;
        return this;
    }

    public WebInventoryBuilder withUpdatedDateTime(LocalDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
        return this;
    }


    public WebInventoryBuilder withLastUpdatedBy(Integer lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
        return this;
    }

    public WebInventory build() {
        WebInventory inventory = new WebInventory();
        inventory.setItemCode(itemCode);
        inventory.setItemName(itemName);
        inventory.setQuantityTransferred(quantityTransferred);
        inventory.setUpdatedDateTime(updatedDateTime != null ? updatedDateTime : LocalDateTime.now());
        inventory.setLastUpdatedBy(lastUpdatedBy);
        return inventory;
    }
}
