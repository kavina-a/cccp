package com.syos.application.dto.mapper;

import com.syos.application.dto.AllocatedRestockDTO;

public class AllocationMapper {
    public static AllocatedRestockDTO toDto(String itemCode, String batchCode, int qty) {
        return new AllocatedRestockDTO(itemCode, batchCode, qty);
    }
}