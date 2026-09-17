package com.eitan.orders.client;

import com.eitan.orders.dto.target.TargetOrderDto;

public interface TargetSystemClient {

    void send(TargetOrderDto order);
}
