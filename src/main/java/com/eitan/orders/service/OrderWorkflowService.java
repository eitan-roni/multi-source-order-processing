package com.eitan.orders.service;

import com.eitan.orders.client.TargetSystemClient;
import com.eitan.orders.dto.target.TargetOrderDto;
import com.eitan.orders.model.CanonicalOrder;
import org.springframework.stereotype.Service;

@Service
public class OrderWorkflowService {

    private final OrderProcessingService orderProcessingService;
    private final TargetSystemClient targetSystemClient;

    public OrderWorkflowService(OrderProcessingService orderProcessingService, TargetSystemClient targetSystemClient) {
        this.orderProcessingService = orderProcessingService;
        this.targetSystemClient = targetSystemClient;
    }

    public TargetOrderDto processAndDeliver(CanonicalOrder order) {
        TargetOrderDto targetOrder = orderProcessingService.process(order);
        targetSystemClient.send(targetOrder);
        return targetOrder;
    }
}
