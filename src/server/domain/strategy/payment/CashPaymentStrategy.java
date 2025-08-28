package server.domain.strategy.payment;

import server.application.dto.PaymentResultDTO;
import server.domain.entity.PaymentMethod;
import server.domain.strategy.payment.intefaces.PaymentStrategy;

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