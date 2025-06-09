package com.syos.domain.strategy.payment;

import com.syos.application.dto.PaymentResultDTO;
import com.syos.domain.entity.PaymentMethod;
import com.syos.domain.strategy.payment.intefaces.PaymentStrategy;

import java.math.BigDecimal;

public class CashPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentResultDTO processPayment(BigDecimal amountDue, BigDecimal tenderedAmount) {
        if (tenderedAmount.compareTo(amountDue) < 0) {
            throw new IllegalArgumentException("Insufficient payment.");
        }
        BigDecimal change = tenderedAmount.subtract(amountDue);
        return new PaymentResultDTO(tenderedAmount, change, PaymentMethod.CASH);
    }
}