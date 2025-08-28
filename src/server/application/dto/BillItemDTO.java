package server.application.dto;

import java.math.BigDecimal;

public class BillItemDTO {
    private String productCode;
    private String productName;
    private int quantity;
    private BigDecimal price;

    public BillItemDTO() {}

    public BillItemDTO(String productCode, String productName, int quantity, BigDecimal price) {
        this.productCode = productCode;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

}