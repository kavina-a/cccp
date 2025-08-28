package server.application.dto;


public class AllocatedRestockDTO {

    private String itemCode;
    private String batchCode;
    private int allocatedQuantity;

    public AllocatedRestockDTO(String itemCode, String batchCode, int allocatedQuantity) {
        this.itemCode = itemCode;
        this.batchCode = batchCode;
        this.allocatedQuantity = allocatedQuantity;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public int getAllocatedQuantity() {
        return allocatedQuantity;
    }

}
