package server.application.dto.request;

import java.time.LocalDate;

public class FindBillRequest {
    private int serialNumber;
    private LocalDate billDate;

    public FindBillRequest(int serialNumber, LocalDate billDate) {
        this.serialNumber = serialNumber;
        this.billDate = billDate;
    }

    public FindBillRequest() {
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public LocalDate getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
    }


}
