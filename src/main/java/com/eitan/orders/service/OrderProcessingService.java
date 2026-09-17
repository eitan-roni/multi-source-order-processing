package com.eitan.orders.service;

import com.eitan.orders.dto.target.TargetOrderDto;
import com.eitan.orders.model.CanonicalOrder;
import com.eitan.orders.model.CountryInfo;

import java.math.BigDecimal;

public class OrderProcessingService {

    private final CountryService countryService;

    public OrderProcessingService(CountryService countryService) {
        this.countryService = countryService;
    }

    public TargetOrderDto process(CanonicalOrder order) {
        CountryInfo countryInfo = countryService.getCountryInfo(order.countryCode());
        BigDecimal totalOrderValue = order.unitPrice().multiply(BigDecimal.valueOf(order.quantity()));

        return new TargetOrderDto(
                order.orderReference(),
                new TargetOrderDto.Customer(
                        order.customerReference(),
                        order.customerFullName(),
                        countryInfo.countryName()
                ),
                order.orderTimestamp(),
                new TargetOrderDto.Product(
                        order.productCode(),
                        order.quantity(),
                        order.unitPrice()
                ),
                totalOrderValue,
                countryInfo.currency()
        );
    }
}
