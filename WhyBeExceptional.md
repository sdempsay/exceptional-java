# Why Exceptional? - A Case for Graceful Exception Handling

## Quick Start

### Add Dependency

```xml
<dependency>
  <groupId>org.dempsay.utils</groupId>
  <artifactId>exceptional</artifactId>
  <version>1.0.3</version>
</dependency>
```

### Import Statements

```java
import org.dempsay.utils.exceptional.api.ExceptionalSupplier;
import org.dempsay.utils.exceptional.api.ExceptionalResponse;
import org.dempsay.utils.exceptional.api.ExceptionalFunction;
import org.dempsay.utils.exceptional.api.ExceptionalAction;
import org.dempsay.utils.exceptional.api.ExceptionalResource;
import org.dempsay.utils.exceptional.api.ExceptionalResourceAction;
```

### Basic Usage

Use `ExceptionalSupplier.of()` with a lambda - no special interfaces needed:

```java
ExceptionalResponse<String> response = ExceptionalSupplier.of(() -> "Hello").execute();
```

The `.of()` method accepts any functional interface that throws `Exception`. For complex cases, use `ExceptionalSupplierCall<R>` explicitly if you need to pass the supplier as a parameter.

## The Problem with Exceptions in Java

Java exceptions force a fundamental choice: handle them immediately with try-catch, or propagate them with throws. Both approaches have significant downsides, especially in modern Java code.

### Exceptions Are Almost Always Non-Recoverable

Most exceptions represent truly exceptional circumstances - network failures, file system errors, external service unavailability. In these cases, recovery is often impossible or inadvisable. Yet Java's checked exceptions force us to pretend we can recover by requiring try-catch blocks or throws declarations.

```java
// The "handle" it immediately approach
try {
    return processData(input);
} catch (IOException e) {
    // What can you actually do here?
    // Log and continue with default? But is that safe?
    // Re-throw? Then why catch in the first place?
    throw new RuntimeException(e);
}

// The "propagate" approach
public String processData(String input) throws IOException {
    // Now every caller must handle it
    // Even if they can't meaningfully recover either
}
```

### The Stream API Awkwardness

The real pain becomes apparent when using Java streams with methods that throw exceptions.

```java
// Without Exceptional - verbose and awkward
List<String> results = inputs.stream()
    .map(input -> {
        try {
            return processData(input);
        } catch (Exception e) {
            throw new UncheckedException(e);
        }
    })
    .collect(Collectors.toList());

// Or using a helper method that re-throws
List<String> results = inputs.stream()
    .map(this.<String>sneakyThrow(data -> processData(data)))
    .collect(Collectors.toList());

private <T> T sneakyThrow(Function<String, T> fn) throws Exception {
    return fn.apply("input");
}
```

Both approaches are clumsy. The first buries the actual logic in exception handling boilerplate. The second uses dubious tricks to bypass the type system.

### Cluttered Code, Hidden Logic

Every try-catch block is a distraction. It:
- Adds visual noise that obscures the actual business logic
- Creates nested indentation that's hard to follow
- Forces error handling to be colocated with logic, even when it shouldn't be

```java
// Normal logic gets buried
public Result processAll(List<Input> inputs) {
    List<Result> results = new ArrayList<>();
    for (Input input : inputs) {
        try {
            Data data = fetchData(input);
            Processed processed = transform(data);
            results.add(aggregate(processed));
        } catch (Exception e) {
            logger.error("Failed to process {}", input, e);
            // What if we want to collect partial results?
            // What if we want to fail fast?
            // The try-catch makes these questions harder
        }
    }
    return new Result(results);
}
```

## How Exceptional Solves This

### 1. Explicit Non-Recovery

`ExceptionalAction`, `ExceptionalSupplier`, and `ExceptionalFunction` all return `ExceptionalResponse`. A failure response makes it clear: this operation might have failed, and you should handle it explicitly.

```java
import org.dempsay.utils.exceptional.api.ExceptionalSupplier;
import org.dempsay.utils.exceptional.api.ExceptionalResponse;

// Clear failure handling
ExceptionalResponse<Result> response = ExceptionalSupplier.of(
    () -> process(input)
).execute();

if (response.wasNoError()) {
    // Only now do we work with the result
    return response.response();
} else {
    // Explicit failure path
    logger.error("Processing failed for input: {}", input);
    return Result.empty();
}
```

### 2. Stream-Friendly

Exceptional wraps work naturally with streams:

```java
import org.dempsay.utils.exceptional.api.ExceptionalSupplier;
import org.dempsay.utils.exceptional.api.ExceptionalResponse;
import java.util.stream.Collectors;

List<Result> results = inputs.stream()
    .map(input -> ExceptionalSupplier.of(() -> processData(input)).execute())
    .filter(ExceptionalResponse::wasNoError)  // Filter to successes
    .map(ExceptionalResponse::response)
    .collect(Collectors.toList());

// Or collect both successes and failures
Map<Boolean, List<Result>> grouped = inputs.stream()
    .map(input -> ExceptionalSupplier.of(() -> processData(input)).execute())
    .collect(Collectors.groupingBy(ExceptionalResponse::wasError,
        Collectors.mapping(ExceptionalResponse::response, Collectors.toList())));
```

### 3. Separation of Concerns

The core logic stays clean. Error handling is delegated:

```java
import org.dempsay.utils.exceptional.api.ExceptionalSupplier;

// Clean processing logic
ExceptionalSupplier.of(() -> {
    Data data = fetchData(input);      // Network call
    Processed processed = transform(data);  // Business logic
    return aggregate(processed);       // Calculation
}).with(error -> logger.error("Failed for input: {}", input, error))
 .execute();
```

### 4. Optional Error Handling

Not all cases need error handling. You can use `ExceptionalSupplier` just to avoid throws declarations:

```java
import org.dempsay.utils.exceptional.api.ExceptionalSupplier;
import org.dempsay.utils.exceptional.api.ExceptionalResponse;

// No error callback needed when you'll check the response
ExceptionalResponse<String> response =
    ExceptionalSupplier.of(() -> potentiallyFailingCall()).execute();

if (response.wasNoError()) {
    return response.response();
}
return defaultValue();
```

### 5. Response Transformation with map()

The `map()` method on `ExceptionalResponse` transforms the value while staying in the `Optional` world. This is useful for chaining transformations without null checks:

```java
import org.dempsay.utils.exceptional.api.ExceptionalSupplier;
import org.dempsay.utils.exceptional.api.ExceptionalResponse;
import java.util.Optional;

// Transform without explicit null check
Optional<String> result = ExceptionalSupplier.of(() -> fetchUser(id))
    .execute()
    .map(user -> user.getName().toUpperCase());

// Chain multiple transformations
Optional<String> details = ExceptionalSupplier.of(() -> fetchOrder(id))
    .execute()
    .map(Order::getCustomer)
    .map(Customer::getName)
    .map(String::toUpperCase);
```

The `map()` method returns `Optional.empty()` automatically if there was an error, making it safe to use in stream pipelines.

### 6. Chaining Operations with then()

The `then()` method chains to another potentially-failing operation, like a flatMap for ExceptionalResponse. If the current response has an error, it short-circuits to failure without executing the next function:

```java
import org.dempsay.utils.exceptional.api.ExceptionalSupplier;
import org.dempsay.utils.exceptional.api.ExceptionalResponse;
import org.dempsay.utils.exceptional.api.ExceptionalFunctionCall;

// Chain multiple potentially-failing operations
ExceptionalResponse<String> result = ExceptionalSupplier.of(() -> fetchUser(id))
    .execute()
    .then(user -> fetchUserProfile(user.getId()))  // returns ExceptionalResponse<Profile>
    .then(profile -> enrichProfile(profile));     // returns ExceptionalResponse<EnrichedProfile>

// With error listener
result.then(next -> processData(next), error -> logger.error("Chain failed", error));
```

Use `then()` when you need to chain operations where each step might throw - the error propagates automatically through the chain.

### 7. Resource Management with Try-With-Resources

`ExceptionalResource` and `ExceptionalResourceAction` handle resources that implement `AutoCloseable` with proper try-with-resources semantics:

```java
import org.dempsay.utils.exceptional.api.ExceptionalResource;
import org.dempsay.utils.exceptional.api.ExceptionalResourceAction;
import org.dempsay.utils.exceptional.api.ExceptionalResponse;

// ExceptionalResource - returns a result
ExceptionalResponse<String> response = ExceptionalResource.<MyResource, String>of(
    () -> new MyResource(),              // Resource creation
    resource -> resource.readFile()      // Resource usage
).execute();

if (response.wasNoError()) {
    String content = response.response();
}

// ExceptionalResourceAction - returns boolean for success/failure
boolean success = ExceptionalResourceAction.<MyResource>of(
    () -> new MyResource(),
    resource -> resource.process()       // Void operation
).execute();

if (success) {
    // Resource was properly closed after action
}
```

These classes ensure resources are always closed (even if exceptions occur) while providing the same exceptional handling patterns as the other Exceptional types.

## When to Use Exceptional

| Scenario | Recommendation |
|----------|---------------|
| Network calls, file I/O, external services | Use Exceptional - failures are external, not your code's fault |
| Stream operations with exception-throwing methods | Use Exceptional - avoids ugly try-catch wrapping |
| Validation failures | Use Exceptional - non-recoverable, just log and continue |
| Configuration errors | Use Exceptional - log and use defaults |
| Programming errors (null pointers, etc.) | Don't catch - let them fail fast |
| Business rule violations | Consider custom exceptions - these are recoverable |

## The Exceptional Pattern

```java
import org.dempsay.utils.exceptional.api.ExceptionalSupplier;
import org.dempsay.utils.exceptional.api.ExceptionalResponse;

// 1. Wrap your code
ExceptionalSupplier.of(() -> riskyOperation())

// 2. Optionally add error handling
.with(error -> logger.warn("Operation failed", error))

// 3. Execute and handle response
ExceptionalResponse<Result> response = execute();
if (response.wasNoError()) {
    use(response.response());
} else {
    fallback();
}
```

For resources:

```java
import org.dempsay.utils.exceptional.api.ExceptionalResource;
import org.dempsay.utils.exceptional.api.ExceptionalResourceAction;

// ExceptionalResource - wrap auto-closeable resources
ExceptionalResource.<Resource, Result>of(
    () -> new Resource(),
    resource -> resource.doWork()
).with(error -> logger.error("Resource error", error))
 .execute();

// ExceptionalResourceAction - for void operations
ExceptionalResourceAction.of(
    () -> new Resource(),
    resource -> resource.doWork()
).execute();
```

The framework doesn't try to hide failures - it makes them explicit, manageable, and stream-friendly.
