package com.eitan.orders.mapper;

import com.eitan.orders.dto.sourceb.SourceBOrderDto;
import com.eitan.orders.model.CanonicalOrder;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SourceBOrderMapperTest {

    private final SourceBOrderMapper mapper = new SourceBOrderMapper();

    @Test
    void mapsAllFieldsToCanonicalOrder() {
        SourceBOrderDto source = new SourceBOrderDto(
                "ORD-20001",
                new SourceBOrderDto.Customer("CUST-842", "Jane", "Miller", "DE"),
                LocalDateTime.of(2026, 9, 1, 11, 15, 0),
                new SourceBOrderDto.Item("P200", 3, new BigDecimal("80.00"))
        );

        CanonicalOrder result = mapper.map(source);

        assertEquals("ORD-20001", result.orderReference());
        assertEquals("CUST-842", result.customerReference());
        assertEquals("Jane Miller", result.customerFullName());
        assertEquals("DE", result.countryCode());
        assertEquals(LocalDateTime.of(2026, 9, 1, 11, 15, 0), result.orderTimestamp());
        assertEquals("P200", result.productCode());
        assertEquals(3, result.quantity());
        assertEquals(new BigDecimal("80.00"), result.unitPrice());
    }

    @Test
    void combinesFirstAndLastNameIntoCustomerFullName() {
        SourceBOrderDto source = new SourceBOrderDto(
                "ORD-20002",
                new SourceBOrderDto.Customer("CUST-843", "Anna", "Schmidt", "DE"),
                LocalDateTime.of(2026, 9, 2, 9, 0, 0),
                new SourceBOrderDto.Item("P201", 1, new BigDecimal("10.00"))
        );

        CanonicalOrder result = mapper.map(source);

        assertEquals("Anna Schmidt", result.customerFullName());
    }
}
