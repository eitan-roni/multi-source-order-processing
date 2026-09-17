package com.eitan.orders.service;

import com.eitan.orders.dto.target.TargetOrderDto;
import com.eitan.orders.model.CanonicalOrder;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderProcessingServiceTest {

    private final OrderProcessingService orderProcessingService = new OrderProcessingService(new CountryService());

    @Test
    void processesCanonicalOrderIntoTargetOrderDto() {
        CanonicalOrder order = new CanonicalOrder(
                "ORD-10001",
                "CUST-501",
                "John Smith",
                "US",
                LocalDateTime.of(2026, 9, 1, 10, 30, 0),
                "P100",
                2,
                new BigDecimal("125.50")
        );

        TargetOrderDto result = orderProcessingService.process(order);

        assertEquals("ORD-10001", result.orderReference());
        assertEquals("CUST-501", result.customer().customerReference());
        assertEquals("John Smith", result.customer().fullName());
        assertEquals("United States", result.customer().country());
        assertEquals("USD", result.currency());
        assertEquals("P100", result.product().code());
        assertEquals(2, result.product().quantity());
        assertEquals(new BigDecimal("125.50"), result.product().unitPrice());
        assertEquals(new BigDecimal("251.00"), result.totalOrderValue());
        assertEquals(LocalDateTime.of(2026, 9, 1, 10, 30, 0), result.orderTimestamp());
    }

    @Test
    void throwsIllegalArgumentExceptionForUnsupportedCountryCode() {
        CanonicalOrder order = new CanonicalOrder(
                "ORD-10002",
                "CUST-502",
                "Ilan Cohen",
                "IL",
                LocalDateTime.of(2026, 9, 1, 12, 0, 0),
                "P101",
                1,
                new BigDecimal("50.00")
        );

        assertThrows(IllegalArgumentException.class, () -> orderProcessingService.process(order));
    }
}
