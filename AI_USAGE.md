# AI Usage — Multi-Source Order Processing

This document records meaningful examples of AI usage during this assignment. AI was used as an assistant to generate recommendations and code; every recommendation was reviewed, and either accepted, modified, or rejected before being included in the project. All AI-generated recommendations and code were reviewed and validated before being included in the project.

## 1. Canonical internal model

**Problem:** The two source systems use very different JSON schemas, but downstream business processing (country/currency enrichment, total value calculation) should be shared between them.

**AI recommendation:** Introduce source-specific DTOs and mappers that normalize both source formats into a shared `CanonicalOrder`.

**Decision:** Accepted.

**Why:** This keeps source-specific schema details isolated from the business logic and makes it easier to add future source systems without changing `OrderProcessingService`.

## 2. Source-specific endpoints

**Problem:** I considered whether both source systems should post to a single generic endpoint.

**AI recommendation:** Use separate endpoints, `POST /api/orders/source-a` and `POST /api/orders/source-b`, because the payload schemas are different and contain no explicit source discriminator.

**Decision:** Accepted, after considering the alternative of one generic endpoint accepting raw `JsonNode`.

**Why:** Separate endpoints preserve type safety, allow Jackson to deserialize directly into the correct DTO, and avoid brittle source-detection logic based on JSON fields. If a production contract required a single endpoint, a source header or query parameter could be used instead.

## 3. Validation and error handling

**Problem:** Invalid requests such as blank IDs, negative quantities, missing nested objects, and unsupported countries needed predictable, consistent handling.

**AI recommendation:** Use Jakarta Bean Validation on the source DTOs, `@Valid` for nested Source B objects, and a centralized `@RestControllerAdvice` for error responses.

**Decision:** Accepted, with a deliberate separation: structural/input validation is handled by Bean Validation on the DTOs, while unsupported country codes remain a business rule inside `CountryService`.

**Why:** This keeps API-boundary validation separate from business rules and avoids duplicating the list of supported countries in DTO annotations.

## 4. Target-system abstraction

**Problem:** The assignment requires delivery to a target system, but no actual target URL, protocol, authentication method, or API contract was provided.

**AI recommendation:** Introduce a `TargetSystemClient` interface and use `LoggingTargetSystemClient` as the assignment implementation.

**Decision:** Accepted.

**Why:** This demonstrates the delivery boundary without inventing an external API. A future HTTP-based implementation can replace the logging implementation without changing `OrderWorkflowService` or the rest of the processing flow.

## 5. Avoiding unnecessary infrastructure

**Problem:** Considered whether Kafka or a database should be added, since the assignment discusses production concerns and an approximate volume of 50,000 orders/day.

**AI recommendation:** Do not add Kafka or persistence to the implementation unless required; document them instead as possible production evolutions.

**Decision:** Accepted.

**Why:** The average throughput implied by 50,000 orders/day is modest, while peak throughput was not specified. peak throughput requirements were not provided, and the assignment has an approximately four-hour time limit. Adding infrastructure would increase complexity without addressing a stated requirement. Asynchronous messaging would become worth revisiting if buffering, retries, decoupling, or significantly higher peak throughput were actual requirements.

## 6. Testing strategy

**Problem:** Needed enough automated coverage to validate the implementation without over-engineering the assignment.

**AI recommendation:** Use focused unit tests for the mappers, country enrichment, and order processing, plus a small set of MockMvc integration tests for the HTTP flow.

**Decision:** Accepted.

**Why:** This provides coverage of both isolated business logic and end-to-end request processing while keeping the test suite small and understandable.
