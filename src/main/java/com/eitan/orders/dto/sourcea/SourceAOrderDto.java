package com.eitan.orders.dto.sourcea;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SourceAOrderDto(
        @NotBlank String orderId,
        @NotBlank String customerId,
        @NotBlank String customerName,
        @NotBlank String country,
        @NotNull LocalDateTime orderDate,
        @NotBlank String productCode,
        @NotNull @Positive Integer quantity,
        @NotNull @Positive BigDecimal unitPrice
) {
}
