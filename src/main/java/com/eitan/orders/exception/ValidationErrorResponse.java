package com.eitan.orders.exception;

import java.util.Map;

public record ValidationErrorResponse(
        String error,
        Map<String, String> violations
) {
}
