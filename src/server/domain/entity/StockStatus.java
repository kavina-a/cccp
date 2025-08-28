package server.domain.entity;

/**
 * Represents the status of stock in the system.
 * This can be used to track the condition and availability of batch items.
 */
public enum StockStatus {
    ACTIVE,
    CONSUMED,
    DAMAGED,
    EXPIRED
}

