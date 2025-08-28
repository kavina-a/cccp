package server.domain.strategy.payment.intefaces;

import server.application.dto.PaymentResultDTO;

import java.math.BigDecimal;

public interface PaymentStrategy {
    PaymentResultDTO processPayment(BigDecimal amountDue, BigDecimal tenderedAmount);
}