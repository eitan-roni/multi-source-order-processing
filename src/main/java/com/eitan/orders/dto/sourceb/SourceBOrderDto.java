package com.eitan.orders.dto.sourceb;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SourceBOrderDto(
        @JsonProperty("order_number") @NotBlank String orderNumber,
        @Valid @NotNull Customer customer,
        @JsonProperty("created_at") @NotNull LocalDateTime createdAt,
        @Valid @NotNull Item item
) {

    public record Customer(
            @NotBlank String id,
            @JsonProperty("first_name") @NotBlank String firstName,
            @JsonProperty("last_name") @NotBlank String lastName,
            @JsonProperty("country_code") @NotBlank String countryCode
    ) {
    }

    public record Item(
            @NotBlank String sku,
            @NotNull @Positive Integer units,
            @NotNull @Positive BigDecimal price
    ) {
    }
}
