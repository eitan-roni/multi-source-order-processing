# Multi-Source Order Processing

## Overview

A Java 21 / Spring Boot application that ingests orders from two external source systems (Source A and Source B), each with its own JSON schema, normalizes them into a shared internal model, enriches them with country and currency information, transforms them into the target-system format, and simulates delivery through a logging-based target client.

## Prerequisites

* Java 21
* Maven Wrapper is included — no local Maven installation is required.

## Build

Windows:

```
.\mvnw.cmd clean package
```

Linux/macOS:

```
./mvnw clean package
```

## Run

Windows:

```
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```
./mvnw spring-boot:run
```

## Tests

Windows:

```
.\mvnw.cmd test
```

Linux/macOS:

```
./mvnw test
```

## Ingestion Endpoints

### `POST /api/orders/source-a`

Accepts an order in Source A's flat JSON schema, maps it to the internal canonical order model, processes it (country/currency enrichment and total value calculation), delivers it to the target system, and returns the resulting target order.

### `POST /api/orders/source-b`

Accepts an order in Source B's nested, snake_case JSON schema, maps it to the same internal canonical order model, and follows the identical processing and delivery flow as Source A.

## Source A Example Request

```json
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
```

curl.exe -X POST "http://localhost:8080/api/orders/source-a" `
  -H "Content-Type: application/json" `
-d '{
"orderId": "ORD-10001",
"customerId": "CUST-501",
"customerName": "John Smith",
"country": "US",
"orderDate": "2026-09-01T10:30:00",
"productCode": "P100",
"quantity": 2,
"unitPrice": 125.50
}'

## Source B Example Request

```json
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
```
curl.exe -X POST "http://localhost:8080/api/orders/source-b" `
  -H "Content-Type: application/json" `
-d '{
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
}'

## Example Target-System Output

```json
{
  "orderReference": "ORD-10001",
  "customer": {
    "customerReference": "CUST-501",
    "fullName": "John Smith",
    "country": "United States"
  },
  "orderTimestamp": "2026-09-01T10:30:00",
  "product": {
    "code": "P100",
    "quantity": 2,
    "unitPrice": 125.50
  },
  "totalOrderValue": 251.00,
  "currency": "USD"
}
```

## Target System Delivery

No real target-system API endpoint or contract was provided as part of this assignment. Delivery is currently simulated by `LoggingTargetSystemClient`, which logs the processed `TargetOrderDto` instead of sending it over HTTP. It implements the `TargetSystemClient` interface, so a real HTTP-based client can be substituted later without changing the rest of the flow.

For demonstration and verification purposes, the processed `TargetOrderDto` is also returned directly in the HTTP response of both endpoints, so the result of the delivery can be inspected without relying solely on log output.

## Error Handling

* Invalid input (e.g. missing required fields, non-positive quantity/price) returns HTTP 400 with an error body of `"VALIDATION_FAILED"` and a map of field-level violations.
* Unsupported country codes return HTTP 400 with an error body of `"BAD_REQUEST"` and the corresponding message.

## Project Structure

```
src/main/java/com/eitan/orders
├── client       # TargetSystemClient abstraction and its logging implementation
├── controller   # REST controllers (OrderController, HealthController)
├── dto
│   ├── sourcea  # Source A request DTO
│   ├── sourceb  # Source B request DTO
│   └── target   # Target system output DTO
├── exception    # Global exception handling and error response records
├── mapper       # Source-specific mappers to the canonical order model
├── model        # CanonicalOrder and CountryInfo
└── service      # CountryService, OrderProcessingService, OrderWorkflowService
```
