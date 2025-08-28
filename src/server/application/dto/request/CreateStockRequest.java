package server.application.dto.request;

import java.time.LocalDateTime;

public class CreateStockRequest {
    private final String itemCode;
    private final String batchCode;
    private final int batchQuantity;
    private final LocalDateTime expiryDate;

    public CreateStockRequest(String itemCode, String batchCode, int batchQuantity, LocalDateTime expiryDate) {
        this.itemCode = itemCode;
        this.batchCode = batchCode;
        this.batchQuantity = batchQuantity;
        this.expiryDate = expiryDate;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public int getBatchQuantity() {
        return batchQuantity;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }
}