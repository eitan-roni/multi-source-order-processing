package com.eitan.orders.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CanonicalOrder(
        String orderReference,
        String customerReference,
        String customerFullName,
        String countryCode,
        LocalDateTime orderTimestamp,
        String productCode,
        int quantity,
        BigDecimal unitPrice
) {
}
