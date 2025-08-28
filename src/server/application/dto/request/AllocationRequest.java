package server.application.dto.request;

import server.domain.entity.StoreStock;
import server.domain.entity.AllocationTarget;

import java.util.List;

public class AllocationRequest {
    private final String itemCode;
    private final int requiredQuantity;
    private final AllocationTarget target;
    private String shelfId;

    private List<StoreStock> availableStock;

    public AllocationRequest(String itemCode, int requiredQuantity, AllocationTarget target) {
        this.itemCode = itemCode;
        this.requiredQuantity = requiredQuantity;
        this.target = target;
    }

    public String getItemCode() {
        return itemCode;
    }

    public int getRequiredQuantity() {
        return requiredQuantity;
    }

    public AllocationTarget getTarget() {
        return target;
    }

    public String getShelfId() {
        return shelfId;
    }

    public void setShelfId(String shelfId) {
        this.shelfId = shelfId;
    }

    public List<StoreStock> getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(List<StoreStock> availableStock) {
        this.availableStock = availableStock;
    }

    /**
     * Determines whether this request requires a shelf ID to be set.
     */
    public boolean requiresShelfId() {
        return this.target == AllocationTarget.SHELF;
    }
}