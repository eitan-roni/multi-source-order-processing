package com.eitan.orders.controller;

import com.eitan.orders.dto.sourcea.SourceAOrderDto;
import com.eitan.orders.dto.sourceb.SourceBOrderDto;
import com.eitan.orders.dto.target.TargetOrderDto;
import com.eitan.orders.mapper.SourceAOrderMapper;
import com.eitan.orders.mapper.SourceBOrderMapper;
import com.eitan.orders.service.OrderProcessingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final SourceAOrderMapper sourceAOrderMapper;
    private final SourceBOrderMapper sourceBOrderMapper;
    private final OrderProcessingService orderProcessingService;

    public OrderController(SourceAOrderMapper sourceAOrderMapper,
                            SourceBOrderMapper sourceBOrderMapper,
                            OrderProcessingService orderProcessingService) {
        this.sourceAOrderMapper = sourceAOrderMapper;
        this.sourceBOrderMapper = sourceBOrderMapper;
        this.orderProcessingService = orderProcessingService;
    }

    @PostMapping("/source-a")
    public TargetOrderDto processSourceAOrder(@RequestBody SourceAOrderDto source) {
        return orderProcessingService.process(sourceAOrderMapper.map(source));
    }

    @PostMapping("/source-b")
    public TargetOrderDto processSourceBOrder(@RequestBody SourceBOrderDto source) {
        return orderProcessingService.process(sourceBOrderMapper.map(source));
    }
}
