package com.syos.domain.entity;

/**
 * Represents the target for allocation of stock.
 * This can either be for a web order or for a shelf.
 */
public enum AllocationTarget {
    WEB("WEB"),
    SHELF("SHELF");

    private final String value;

    AllocationTarget(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AllocationTarget fromValue(String value) {
        for (AllocationTarget target : AllocationTarget.values()) {
            if (target.value.equalsIgnoreCase(value)) {
                return target;
            }
        }
        throw new IllegalArgumentException("Unknown allocation target: " + value);
    }
}
