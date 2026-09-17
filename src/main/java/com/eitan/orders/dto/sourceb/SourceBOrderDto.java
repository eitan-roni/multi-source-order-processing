package com.eitan.orders.dto.sourceb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SourceBOrderDto(
        @JsonProperty("order_number") String orderNumber,
        Customer customer,
        @JsonProperty("created_at") LocalDateTime createdAt,
        Item item
) {

    public record Customer(
            String id,
            @JsonProperty("first_name") String firstName,
            @JsonProperty("last_name") String lastName,
            @JsonProperty("country_code") String countryCode
    ) {
    }

    public record Item(
            String sku,
            int units,
            BigDecimal price
    ) {
    }
}
