package com.syos.domain.strategy.payment.intefaces;

import com.syos.application.dto.PaymentResultDTO;

import java.math.BigDecimal;

public interface PaymentStrategy {
    PaymentResultDTO processPayment(BigDecimal amountDue, BigDecimal tenderedAmount);
}