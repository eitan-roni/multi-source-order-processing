package com.eitan.orders.mapper;

import com.eitan.orders.dto.sourcea.SourceAOrderDto;
import com.eitan.orders.model.CanonicalOrder;

public class SourceAOrderMapper {

    public CanonicalOrder map(SourceAOrderDto source) {
        return new CanonicalOrder(
                source.orderId(),
                source.customerId(),
                source.customerName(),
                source.country(),
                source.orderDate(),
                source.productCode(),
                source.quantity(),
                source.unitPrice()
        );
    }
}
