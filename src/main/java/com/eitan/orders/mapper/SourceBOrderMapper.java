package com.eitan.orders.mapper;

import com.eitan.orders.dto.sourceb.SourceBOrderDto;
import com.eitan.orders.model.CanonicalOrder;

public class SourceBOrderMapper {

    public CanonicalOrder map(SourceBOrderDto source) {
        return new CanonicalOrder(
                source.orderNumber(),
                source.customer().id(),
                source.customer().firstName() + " " + source.customer().lastName(),
                source.customer().countryCode(),
                source.createdAt(),
                source.item().sku(),
                source.item().units(),
                source.item().price()
        );
    }
}
