package server.application.dto.mapper;

import server.application.dto.AllocatedRestockDTO;

public class AllocationMapper {
    public static AllocatedRestockDTO toDto(String itemCode, String batchCode, int qty) {
        return new AllocatedRestockDTO(itemCode, batchCode, qty);
    }
}