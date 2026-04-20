# Product Requirements Document (PRD)

## Project: Exceptional - Exception Handling Framework

### Overview
A Java utility framework that provides graceful exception handling for functional interfaces by wrapping them in try-catch blocks and returning a response object instead of throwing exceptions.

### Purpose
To provide a clean API for executing code that may throw exceptions without forcing callers to handle exceptions via try-catch blocks or declares clauses.

---

## Functional Requirements

### FR1: ExceptionalAction
Execute a void action that may throw an exception.

**API:**
```java
ExceptionalAction.of(() -> { /* code that may throw */ })
    .with(listener)  // optional error listener
    .execute();      // returns boolean: true if successful, false if exception occurred
```

**Behavior:**
- Executes the action
- Returns `true` on success, `false` on any exception
- Optionally calls `ExceptionalListener.onError()` if listener is provided

---

### FR2: ExceptionalSupplier
Execute a supplier (no input, returns value) that may throw an exception.

**API:**
```java
ExceptionalSupplier.of(() -> { /* code that may throw and return R */ })
    .with(listener)  // optional error listener
    .execute();      // returns ExceptionalResponse<R>
```

**Behavior:**
- Executes the supplier
- Returns `ExceptionalResponse.success(result)` on success
- Returns `ExceptionalResponse.failure()` on exception
- Optionally calls `ExceptionalListener.onError()` if listener is provided

---

### FR3: ExceptionalFunction
Execute a function (accepts input, returns value) that may throw an exception.

**API:**
```java
ExceptionalFunction.of((input) -> { /* code that may throw */ })
    .with(listener)  // optional error listener
    .execute(input); // returns ExceptionalResponse<R>
```

**Behavior:**
- Applies input to the function
- Returns `ExceptionalResponse.success(result)` on success
- Returns `ExceptionalResponse.failure()` on exception
- Optionally calls `ExceptionalListener.onError()` if listener is provided

---

### FR4: ExceptionalListener
Callback interface for error handling.

**API:**
```java
public interface ExceptionalListener {
    void onError(Exception error);
}
```

**Behavior:**
- Called when an exception occurs in any of the three wrapper classes
- May be null (no error callback)

---

### FR5: ExceptionalResponse
Response wrapper that encapsulates success or failure.

**API:**
```java
public record ExceptionalResponse<R>(R response, boolean wasError) {
    Optional<R> safeResponse();  // returns Optional.ofNullable(response)
    boolean wasError();          // returns wasError field
    boolean wasNoError();        // returns !wasError
    static <R> ExceptionalResponse<R> success(R result);
    static <R> ExceptionalResponse<R> failure();
    static Predicate<ExceptionalResponse<?>> hasError();
}
```

**Behavior:**
- `success(result)`: Creates response with result and `wasError = false`
- `failure()`: Creates response with `null` and `wasError = true`
- `safeResponse()`: Returns `Optional.ofNullable(response)` to safely access result
- `wasNoError()`: Convenience method for `!wasError`

---

### FR6: Closeable support
Wrap the lifecycle of an AutoCloseable resource (creation, usage, and closing) to avoid explicit try-with-resources blocks.

**API 1: ExceptionalResource (Value returning)**
```java
ExceptionalResponse<R> response = ExceptionalResource.of(
    () -> new MyResource(), // Supplier<T> resourceSupplier
    res -> res.doWork()     // Function<T, R> resourceUsage
)
    .with(listener)
    .execute();
```

**API 2: ExceptionalResourceAction (Void)**
```java
boolean success = ExceptionalResourceAction.of(
    () -> new MyResource(), // Supplier<T> resourceSupplier
    res -> res.doWorkVoid() // Consumer<T> resourceUsage
)
    .with(listener)
    .execute();
```

**Behavior:**
- Ensures `resource.close()` is called regardless of whether usage succeeds.
- Returns success/true if creation, usage, and closing succeed.
- Returns failure/false if any stage throws an exception.
- Optionally calls `ExceptionalListener.onError()` if an exception occurs at any stage.
- Handles suppressed exceptions if both usage and closing fail, reporting the primary exception.

## Non-Functional Requirements

### NFR1: Java Version
- Java 21+ (for records and text blocks)

### NFR2: Testing
- Unit tests using JUnit 5
- Tests must cover:
  - Successful execution path
  - Exceptional execution path
  - Error listener callback

### NFR3: Build System
- Maven project
- Parent POM: `org.dempsay.maven:dempsay-parent:1.1.0-SNAPSHOT`

---

## Package Structure

```
src/main/java/org/dempsay/utils/exceptional/api/
├── ExceptionalAction.java
├── ExceptionalActionCall.java (functional interface)
├── ExceptionalFunction.java
├── ExceptionalFunctionCall.java (functional interface)
├── ExceptionalSupplier.java
├── ExceptionalSupplierCall.java (functional interface)
├── ExceptionalListener.java (interface)
└── ExceptionalResponse.java (record)
```

---

## Test Coverage

Tests in `src/test/java/org/dempsay/utils/exceptional/`:

1. **TestExceptionAction**
   - `testSuccessfulAction()`: Verify successful action returns true
   - `testUnsuccessfulAction()`: Verify failed action returns false
   - `testUnsuccessfulActionListener()`: Verify listener receives exception

2. **TestExceptionSupplier**
   - `testSuccessfulSupplier()`: Verify successful supplier returns correct response
   - `testUnsuccessfulSupplier()`: Verify failure returns null response
   - `testUnsuccessfulSupplierListener()`: Verify listener receives exception

3. **TestExceptionFunction**
   - `testSuccessfulFunction()`: Verify successful function returns correct response
   - `testUnsuccessfulFunction()`: Verify failure returns null response
   - `testUnsuccessfulFunctionListener()`: Verify listener receives exception

---

## Example Usage

```java
// Action
boolean success = ExceptionalAction.of(() -> {
    // code that might throw
}).execute();

// Supplier with listener
ExceptionalResponse<String> response = ExceptionalSupplier.of(() -> {
    return fetchData();
}).with(error -> logger.error("Fetch failed", error)).execute();

// Function
ExceptionalResponse<Integer> result = ExceptionalFunction.of(Integer::parseInt)
    .execute("123");
```
