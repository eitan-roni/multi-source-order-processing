package com.eitan.orders.exception;

public record BadRequestErrorResponse(
        String error,
        String message
) {
}
