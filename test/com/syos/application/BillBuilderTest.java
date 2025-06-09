
package com.syos.application;

import com.syos.application.dto.builder.BillBuilder;
import com.syos.domain.entity.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BillBuilderTest {

    @Test
    void shouldBuildBillWithAllFields() {
        Bill bill = new BillBuilder()
                .withSerialNumber(1001)
                .withBillDate(LocalDate.of(2025, 6, 8))
                .withCustomer(new Customer())
                .withCashTendered(BigDecimal.valueOf(1000))
                .withTotalAmount(BigDecimal.valueOf(950))
                .withChangeAmount(BigDecimal.valueOf(50))
                .withBillItems(List.of(new BillItem()))
                .withCreatedAt(LocalDateTime.of(2025, 6, 8, 10, 0))
                .withBillType(BillType.WEB_BILL)
                .withPaymentMethod(PaymentMethod.CARD)
                .build();

        assertEquals(1001, bill.getSerialNumber());
        assertEquals(BigDecimal.valueOf(1000), bill.getCashTendered());
        assertEquals(BigDecimal.valueOf(950), bill.getTotalAmount());
        assertEquals(BigDecimal.valueOf(50), bill.getChangeAmount());
        assertEquals(1, bill.getItems().size());
        assertNotNull(bill.getCustomer());
        assertEquals(BillType.WEB_BILL, bill.getBillType());
        assertEquals(PaymentMethod.CARD, bill.getPaymentMethod());
    }

    @Test
    void shouldBuildBillWithDefaults() {
        Bill bill = new BillBuilder().withSerialNumber(1).build();

        assertEquals(0, bill.getItems().size());
        assertNotNull(bill.getBillDate());
        assertNotNull(bill.getCreatedAt());
        assertEquals(BigDecimal.ZERO, bill.getChangeAmount());
    }

    @Test
    void shouldHandleNullBillItemsGracefully() {
        Bill bill = new BillBuilder()
                .withSerialNumber(2)
                .withBillItems(null)
                .build();
        assertNotNull(bill.getItems());
        assertTrue(bill.getItems().isEmpty());
    }

    @Test
    void shouldDefaultNullDatesToNow() {
        Bill bill = new BillBuilder()
                .withSerialNumber(3)
                .withBillDate(null)
                .withCreatedAt(null)
                .build();

        assertNotNull(bill.getBillDate());
        assertNotNull(bill.getCreatedAt());
    }

    @Test
    void shouldDefaultNullChangeAmountToZero() {
        Bill bill = new BillBuilder()
                .withSerialNumber(4)
                .withChangeAmount(null)
                .build();

        assertEquals(BigDecimal.ZERO, bill.getChangeAmount());
    }

    @Test
    void shouldSupportPartialFieldPopulation() {
        Bill bill = new BillBuilder()
                .withSerialNumber(9999)
                .withCashTendered(BigDecimal.valueOf(1200))
                .build();

        assertEquals(9999, bill.getSerialNumber());
        assertEquals(BigDecimal.valueOf(1200), bill.getCashTendered());
    }

    @Test
    void shouldAllowReusingBuilderSafely() {
        BillBuilder builder = new BillBuilder()
                .withSerialNumber(1);

        Bill bill1 = builder.build();
        Bill bill2 = builder.withSerialNumber(2).build();

        assertEquals(1, bill1.getSerialNumber());
        assertEquals(2, bill2.getSerialNumber());
    }

    @Test
    void shouldAcceptNegativeAmounts() {
        Bill bill = new BillBuilder()
                .withSerialNumber(6)
                .withCashTendered(BigDecimal.valueOf(-500))
                .withTotalAmount(BigDecimal.valueOf(-1000))
                .build();

        assertEquals(BigDecimal.valueOf(-500), bill.getCashTendered());
        assertEquals(BigDecimal.valueOf(-1000), bill.getTotalAmount());
    }

    @Test
    void shouldHandleNullPaymentMethod() {
        Bill bill = new BillBuilder()
                .withSerialNumber(7)
                .withPaymentMethod(null)
                .build();

        assertNull(bill.getPaymentMethod());
    }

    @Test
    void shouldHandleNullCustomer() {
        Bill bill = new BillBuilder()
                .withSerialNumber(8)
                .withCustomer(null)
                .build();

        assertNull(bill.getCustomer());
    }
}
