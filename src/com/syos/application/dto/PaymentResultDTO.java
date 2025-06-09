package com.syos.application.dto;

import com.syos.domain.entity.PaymentMethod;

import java.math.BigDecimal;

public class PaymentResultDTO {
    private final BigDecimal tenderedAmount;
    private final BigDecimal change;
    private final PaymentMethod method;

    public PaymentResultDTO(BigDecimal tenderedAmount, BigDecimal change, PaymentMethod method) {
        this.tenderedAmount = tenderedAmount;
        this.change = change;
        this.method = method;
    }

    public BigDecimal getTenderedAmount() {
        return tenderedAmount;
    }

    public BigDecimal getChange() {
        return change;
    }

    public PaymentMethod getMethod() {
        return method;
    }
}