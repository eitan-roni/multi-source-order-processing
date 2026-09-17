# Design Notes — Multi-Source Order Processing

## Architecture

```
Source A JSON / Source B JSON
      -> source-specific DTO
      -> source-specific mapper
      -> CanonicalOrder
      -> OrderWorkflowService
      -> OrderProcessingService
      -> TargetOrderDto
      -> TargetSystemClient
```

* **Source-specific DTO** (`SourceAOrderDto`, `SourceBOrderDto`) — a typed representation of each source system's own JSON schema. Field names and Jackson annotations exist purely to match that source's contract.
* **Source-specific mapper** (`SourceAOrderMapper`, `SourceBOrderMapper`) — converts a source DTO into the shared `CanonicalOrder`. This is the only place that knows about source-specific field names, nesting, or naming conventions (e.g. Source B's `snake_case` and nested `customer`/`item` objects).
* **`CanonicalOrder`** — a source-agnostic internal representation of an order. All downstream logic operates on this model only.
* **`OrderWorkflowService`** — orchestrates the use case: process the order, then deliver it. It has no business rules of its own.
* **`OrderProcessingService`** — applies business rules: resolves country name and currency via `CountryService`, and calculates `totalOrderValue`.
* **`TargetOrderDto`** — the outbound representation expected by the target system.
* **`TargetSystemClient`**  an abstraction for delivering TargetOrderDto, allowing OrderWorkflowService to remain independent of the concrete delivery mechanism.

## Processing Flow

1. A client sends a `POST` request to `/api/orders/source-a` or `/api/orders/source-b`.
2. Jackson deserializes the request body into the corresponding source DTO (`SourceAOrderDto` or `SourceBOrderDto`).
3. Jakarta Bean Validation validates the DTO (and, for Source B, the nested `customer`/`item` objects via `@Valid`) before the controller method body runs.
4. `OrderController` calls the matching mapper (`SourceAOrderMapper` or `SourceBOrderMapper`), which normalizes the source-specific schema into a `CanonicalOrder`.
5. `OrderController` passes the `CanonicalOrder` to `OrderWorkflowService.processAndDeliver(...)`.
6. `OrderWorkflowService` calls `OrderProcessingService.process(...)`, which uses `CountryService` to resolve the full country name and currency for the order's country code, and calculates `totalOrderValue` as `unitPrice * quantity`. The result is a `TargetOrderDto`.
7. `OrderWorkflowService` passes the `TargetOrderDto` to `TargetSystemClient.send(...)`. The current implementation, `LoggingTargetSystemClient`, logs the order instead of calling a real API, because no target-system endpoint or contract was provided as part of this assignment.
8. `OrderWorkflowService` returns the same `TargetOrderDto` back up the call chain, and `OrderController` returns it as the HTTP response body — so the result of processing and (simulated) delivery can be inspected directly from the API call, without relying on log output.

## Important Technical Decisions

### Canonical internal model

Both source schemas are converted into `CanonicalOrder` before any business rule (country/currency enrichment, total value calculation) is applied. This keeps `OrderProcessingService` completely unaware of source-specific field names or JSON shapes. Adding a new source system only requires a new DTO and mapper; the processing and delivery logic does not change.

### Separate endpoints

`POST /api/orders/source-a` and `POST /api/orders/source-b` are separate because the two payloads have different, source-specific JSON schemas and neither payload contains an explicit field identifying which source it came from. Each source system is expected to call its own endpoint. If a production contract required a single ingestion point, an alternative would be one endpoint that accepts a source identifier via header or query parameter and dispatches to the correct DTO/mapper — this was not needed here since the source is already known from the URL the caller uses.

### Java records

All DTOs (`SourceAOrderDto`, `SourceBOrderDto`, `TargetOrderDto`) and simple internal models (`CanonicalOrder`, `CountryInfo`) are Java 21 records. They are pure, immutable data carriers with no behavior, which is exactly what records are designed for — concise declarations, generated equals/hashCode/toString, and no risk of accidental mutation.

### BigDecimal

Monetary values (`unitPrice`, `totalOrderValue`) use `BigDecimal` instead of `double`. `double` uses binary floating-point representation and cannot exactly represent most decimal fractions, which can introduce rounding errors in financial calculations. `BigDecimal` provides decimal arithmetic with explicit precision and rounding behavior, making it appropriate for monetary calculations.

### Validation

Jakarta Bean Validation annotations (`@NotBlank`, `@NotNull`, `@Positive`, `@Valid` for nested objects) are applied directly on the source DTOs, at the API boundary, so malformed input is rejected before it reaches any business logic. `GlobalExceptionHandler`, a `@RestControllerAdvice`, centralizes translation of `MethodArgumentNotValidException` (validation failures) and `IllegalArgumentException` (e.g. unsupported country code) into consistent HTTP 400 responses, instead of scattering error-handling logic across controllers.

### Country reference data

The three supported country mappings (`US`, `GB`, `DE`) are kept in an in-memory `Map` inside `CountryService` because the assignment specifies a small, static reference set. In production, this data would more likely come from configuration, a database table, or an external reference-data service, especially if the set of supported countries were large or changed independently of application deployments. `CountryService` already isolates this concern behind a single method (`getCountryInfo`), so the in-memory map could be swapped for another data source without affecting `OrderProcessingService`.

### Target system abstraction

`TargetSystemClient` is an interface so that `OrderWorkflowService` depends only on the ability to deliver a `TargetOrderDto`, not on how that delivery happens. Since no real target-system API contract (protocol, authentication, endpoint) was provided, the assignment includes `LoggingTargetSystemClient`, which logs the processed order to demonstrate the flow end-to-end. A real implementation (e.g. an HTTP client) could be added later and swapped in via Spring without changing `OrderWorkflowService` or the controller.

## Alternatives Considered

* **Directly mapping each source DTO into `TargetOrderDto` without a canonical model.** Rejected because business logic (country/currency enrichment, total value calculation) would have to be duplicated or coupled to every source-specific schema, making it harder to keep consistent and harder to extend with new sources.
* **One generic endpoint accepting raw `JsonNode` and detecting the source from JSON fields.** Rejected because it discards compile-time type safety, pushes schema knowledge into ad hoc detection logic, and makes it easy for a source to silently be misidentified.
* **Kafka or another message broker for ingestion.** Not implemented. At approximately 50,000 orders/day, the average throughput is modest. Given the assignment scope and the absence of peak-rate or buffering requirements, a synchronous HTTP design was chosen. If peak throughput, buffering, retry, or decoupling requirements were significantly higher, a message broker such as Kafka would be considered.
* **Database persistence of orders.** Not implemented, because no persistence or idempotency requirements were specified in the assignment. Adding a data store without a clear need (query patterns, retention, uniqueness rules) would be speculative.

## Assumptions

* Each source system calls its own dedicated endpoint (`/api/orders/source-a` or `/api/orders/source-b`); the application does not attempt to infer the source from the payload.
* `orderId` (Source A) / `order_number` (Source B) uniquely identifies an order, but duplicate-order handling was not implemented, since idempotency requirements were not specified.
* The provided timestamps (`orderDate`, `created_at`) contain no timezone or offset information, so `LocalDateTime` is used throughout. In production, the timezone contract should be clarified with the source systems, and `Instant` or `OffsetDateTime` (with UTC as the wire format) may be preferable to avoid ambiguity.
* An unsupported country code causes the order to be rejected with HTTP 400 rather than processed with partial data.
* `quantity` and `unitPrice`/`price` must be strictly greater than zero.
* No real target-system API details (protocol, endpoint, authentication) were provided, so delivery is simulated by logging via `LoggingTargetSystemClient`.
* The three provided country mappings (`US`, `GB`, `DE`) are treated as the complete supported set for this assignment.

## Production Questions

* What protocol/API does the target system expose (REST, SOAP, message queue, file-based)?
* What authentication/authorization is required to call the target system?
* What are the timeout and retry requirements for target-system delivery?
* Must order processing and delivery be synchronous (as implemented) or asynchronous?
* What should happen when the target system is unavailable — retry, queue, fail the request, or dead-letter?
* Are duplicate orders possible from a source system, and what is the required idempotency rule?
* Can an order be updated or cancelled after it has been submitted?
* What are the expected peak rates, not just the average daily volume of ~50,000 orders/day?
* What timezone are source timestamps actually expressed in?
* How is country/currency reference data maintained and kept up to date in production?
* Should invalid orders be rejected immediately (as implemented), stored for manual review, or routed to a dead-letter mechanism?
* What observability (metrics, tracing), audit trail, and data retention requirements exist?

## Scaling and Evolution

### More source systems

Add a new source-specific DTO under `dto`, a corresponding mapper under `mapper` that converts it into the existing `CanonicalOrder`, and a new endpoint (or dispatch rule) in the controller. `OrderProcessingService`, `OrderWorkflowService`, and `TargetSystemClient` remain unchanged, since they only depend on `CanonicalOrder`/`TargetOrderDto`.

### Processing volume increases

If throughput requirements grew well beyond the current scope, the flow could evolve to:

```
Source systems
  -> API / ingestion layer
  -> Kafka or another durable message broker
  -> horizontally scalable processing consumers
  -> target system
```

This would decouple ingestion from processing, provide buffering during spikes or target-system downtime, allow processing consumers to be scaled independently, and support retries without holding the original HTTP request open. This is not implemented now because current volume does not warrant it.

### Country/reference data grows

The static in-memory map in `CountryService` could be replaced with data sourced from application configuration, a database table, a cache, or an external reference-data service, without changing its public method signature or any of its callers.

### Target-system integration becomes real

Once a real target-system contract is available, `LoggingTargetSystemClient` can be replaced (or complemented) by a new implementation, such as `HttpTargetSystemClient`, of the existing `TargetSystemClient` interface. `OrderWorkflowService` and the controller would not need to change.

## Testing Strategy

* **Mapper unit tests** (`SourceAOrderMapperTest`, `SourceBOrderMapperTest`) verify that every field is correctly mapped from each source DTO into `CanonicalOrder`, including Source B's `firstName`/`lastName` combination into `customerFullName`.
* **`CountryServiceTest`** verifies the three supported country/currency mappings and that an unsupported code throws `IllegalArgumentException`.
* **`OrderProcessingServiceTest`** verifies enrichment and total-value calculation end-to-end using a real `CountryService`, plus the unsupported-country failure case.
* **`OrderControllerTest`** provides `MockMvc`-based integration tests against the real Spring application context (no mocked business services), covering successful Source A and Source B requests, a validation failure (negative quantity), and an unsupported country code, asserting both the HTTP status and the structured error/response body.
