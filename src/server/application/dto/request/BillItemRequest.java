package server.application.dto.request;

import java.math.BigDecimal;

public class BillItemRequest {
    private String productCode;
    private int quantity;
    private BigDecimal pricePerItem;

    public BillItemRequest(String productCode, int quantity, BigDecimal pricePerItem) {
        this.productCode = productCode;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPricePerItem() {
        return pricePerItem;
    }

    public void setPricePerItem(BigDecimal pricePerItem) {
        this.pricePerItem = pricePerItem;
    }


}
