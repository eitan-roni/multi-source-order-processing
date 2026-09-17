package com.eitan.orders.client;

import com.eitan.orders.dto.target.TargetOrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingTargetSystemClient implements TargetSystemClient {

    private static final Logger log = LoggerFactory.getLogger(LoggingTargetSystemClient.class);

    @Override
    public void send(TargetOrderDto order) {
        log.info("Processed order ready for target system: {}", order);
    }
}
