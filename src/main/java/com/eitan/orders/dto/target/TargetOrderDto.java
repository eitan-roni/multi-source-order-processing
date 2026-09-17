package com.eitan.orders.dto.target;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TargetOrderDto(
        String orderReference,
        Customer customer,
        LocalDateTime orderTimestamp,
        Product product,
        BigDecimal totalOrderValue,
        String currency
) {

    public record Customer(
            String customerReference,
            String fullName,
            String country
    ) {
    }

    public record Product(
            String code,
            int quantity,
            BigDecimal unitPrice
    ) {
    }
}
