package server.application.mapper;

import server.application.dto.AllocatedRestockDTO;
import server.domain.entity.StoreStock;

import java.util.List;
import java.util.stream.Collectors;

public class AllocationMapper {

    // From StoreStock entity to DTO
    public static AllocatedRestockDTO toDTO(StoreStock storeStock, int allocatedQuantity) {
        return new AllocatedRestockDTO(
                storeStock.getItemCode(),
                storeStock.getBatchCode(),
                allocatedQuantity
        );
    }

    // Bulk conversion
    public static List<AllocatedRestockDTO> toDTOList(List<StoreStock> stocks, int quantityPerBatch) {
        return stocks.stream()
                .map(stock -> toDTO(stock, quantityPerBatch))
                .collect(Collectors.toList());
    }

    // Inverse mapper (optional)
    public static StoreStock toEntity(AllocatedRestockDTO dto) {
        StoreStock stock = new StoreStock();
        stock.setItemCode(dto.getItemCode());
        stock.setBatchCode(dto.getBatchCode());
        stock.setCurrentStock(dto.getAllocatedQuantity());
        return stock;
    }
}