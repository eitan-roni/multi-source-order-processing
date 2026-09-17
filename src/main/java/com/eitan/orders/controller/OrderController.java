package com.eitan.orders.controller;

import com.eitan.orders.dto.sourcea.SourceAOrderDto;
import com.eitan.orders.dto.sourceb.SourceBOrderDto;
import com.eitan.orders.dto.target.TargetOrderDto;
import com.eitan.orders.mapper.SourceAOrderMapper;
import com.eitan.orders.mapper.SourceBOrderMapper;
import com.eitan.orders.service.OrderWorkflowService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final SourceAOrderMapper sourceAOrderMapper;
    private final SourceBOrderMapper sourceBOrderMapper;
    private final OrderWorkflowService orderWorkflowService;

    public OrderController(SourceAOrderMapper sourceAOrderMapper,
                            SourceBOrderMapper sourceBOrderMapper,
                            OrderWorkflowService orderWorkflowService) {
        this.sourceAOrderMapper = sourceAOrderMapper;
        this.sourceBOrderMapper = sourceBOrderMapper;
        this.orderWorkflowService = orderWorkflowService;
    }

    @PostMapping("/source-a")
    public TargetOrderDto processSourceAOrder(@Valid @RequestBody SourceAOrderDto source) {
        return orderWorkflowService.processAndDeliver(sourceAOrderMapper.map(source));
    }

    @PostMapping("/source-b")
    public TargetOrderDto processSourceBOrder(@Valid @RequestBody SourceBOrderDto source) {
        return orderWorkflowService.processAndDeliver(sourceBOrderMapper.map(source));
    }
}
