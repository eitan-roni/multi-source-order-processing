package com.eitan.orders.dto.sourcea;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SourceAOrderDto(
        String orderId,
        String customerId,
        String customerName,
        String country,
        LocalDateTime orderDate,
        String productCode,
        int quantity,
        BigDecimal unitPrice
) {
}
