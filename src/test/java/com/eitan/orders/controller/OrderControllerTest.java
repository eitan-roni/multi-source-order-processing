package com.eitan.orders.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void validSourceARequestReturnsTargetOrder() throws Exception {
        String requestBody = """
                {
                  "orderId": "ORD-10001",
                  "customerId": "CUST-501",
                  "customerName": "John Smith",
                  "country": "US",
                  "orderDate": "2026-09-01T10:30:00",
                  "productCode": "P100",
                  "quantity": 2,
                  "unitPrice": 125.50
                }
                """;

        mockMvc.perform(post("/api/orders/source-a")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderReference").value("ORD-10001"))
                .andExpect(jsonPath("$.customer.customerReference").value("CUST-501"))
                .andExpect(jsonPath("$.customer.fullName").value("John Smith"))
                .andExpect(jsonPath("$.customer.country").value("United States"))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.product.code").value("P100"))
                .andExpect(jsonPath("$.product.quantity").value(2))
                .andExpect(jsonPath("$.product.unitPrice").value(125.50))
                .andExpect(jsonPath("$.totalOrderValue").value(251.00));
    }

    @Test
    void validSourceBRequestReturnsTargetOrder() throws Exception {
        String requestBody = """
                {
                  "order_number": "ORD-20001",
                  "customer": {
                    "id": "CUST-842",
                    "first_name": "Jane",
                    "last_name": "Miller",
                    "country_code": "DE"
                  },
                  "created_at": "2026-09-01T11:15:00",
                  "item": {
                    "sku": "P200",
                    "units": 3,
                    "price": 80.00
                  }
                }
                """;

        mockMvc.perform(post("/api/orders/source-b")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderReference").value("ORD-20001"))
                .andExpect(jsonPath("$.customer.customerReference").value("CUST-842"))
                .andExpect(jsonPath("$.customer.fullName").value("Jane Miller"))
                .andExpect(jsonPath("$.customer.country").value("Germany"))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.product.code").value("P200"))
                .andExpect(jsonPath("$.product.quantity").value(3))
                .andExpect(jsonPath("$.totalOrderValue").value(240.00));
    }

    @Test
    void invalidSourceARequestWithNegativeQuantityReturnsValidationError() throws Exception {
        String requestBody = """
                {
                  "orderId": "ORD-10001",
                  "customerId": "CUST-501",
                  "customerName": "John Smith",
                  "country": "US",
                  "orderDate": "2026-09-01T10:30:00",
                  "productCode": "P100",
                  "quantity": -2,
                  "unitPrice": 125.50
                }
                """;

        mockMvc.perform(post("/api/orders/source-a")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.violations.quantity").exists());
    }

    @Test
    void unsupportedCountryCodeReturnsBadRequestError() throws Exception {
        String requestBody = """
                {
                  "orderId": "ORD-10002",
                  "customerId": "CUST-502",
                  "customerName": "Ilan Cohen",
                  "country": "IL",
                  "orderDate": "2026-09-01T10:30:00",
                  "productCode": "P101",
                  "quantity": 1,
                  "unitPrice": 50.00
                }
                """;

        mockMvc.perform(post("/api/orders/source-a")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }
}
