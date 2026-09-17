package com.eitan.orders.mapper;

import com.eitan.orders.dto.sourcea.SourceAOrderDto;
import com.eitan.orders.model.CanonicalOrder;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SourceAOrderMapperTest {

    private final SourceAOrderMapper mapper = new SourceAOrderMapper();

    @Test
    void mapsAllFieldsToCanonicalOrder() {
        SourceAOrderDto source = new SourceAOrderDto(
                "ORD-10001",
                "CUST-501",
                "John Smith",
                "US",
                LocalDateTime.of(2026, 9, 1, 10, 30, 0),
                "P100",
                2,
                new BigDecimal("125.50")
        );

        CanonicalOrder result = mapper.map(source);

        assertEquals("ORD-10001", result.orderReference());
        assertEquals("CUST-501", result.customerReference());
        assertEquals("John Smith", result.customerFullName());
        assertEquals("US", result.countryCode());
        assertEquals(LocalDateTime.of(2026, 9, 1, 10, 30, 0), result.orderTimestamp());
        assertEquals("P100", result.productCode());
        assertEquals(2, result.quantity());
        assertEquals(new BigDecimal("125.50"), result.unitPrice());
    }
}
