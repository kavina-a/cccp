package server.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BillDTO {
    private String serialNumber;
    private LocalDateTime date;
    private BigDecimal totalAmount;
    private BigDecimal cashTendered;
    private BigDecimal change;
    private List<BillItemDTO> items;

    public BillDTO() {
    }

    public BillDTO(String serialNumber, LocalDateTime date, BigDecimal totalAmount,
                   BigDecimal cashTendered, BigDecimal change, List<BillItemDTO> items) {
        this.serialNumber = serialNumber;
        this.date = date;
        this.totalAmount = totalAmount;
        this.cashTendered = cashTendered;
        this.change = change;
        this.items = items;
    }

}