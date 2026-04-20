# Developer Instructions

## Build & Test
```bash
mvn test           # Run all tests
mvn verify         # Build + test + package
mvn clean.install  # Clean build
```

## Project Structure
- **API package**: `org.dempsay.utils.exceptional.api`
  - `ExceptionalSupplier<R>` - main entry point
  - `ExceptionalFunction<R,T>` - function variant
  - `ExceptionalAction` - action variant
  - `ExceptionalResponse<R>` - wrapper with `wasNoError()`, `wasError()`, `safeResponse()`
  - XCall interfaces for lambda wrapping

## Test Patterns
```java
var response = ExceptionalSupplier.of(() -> riskyCall())
    .with(exception -> logger.error("failed", exception))
    .execute();
if (response.wasNoError()) {
    use(response.response());
}
```

## Notes
- Parent POM: `org.dempsay.maven:maven-parent:1.1.0-SNAPSHOT`
- JUnit version: 5.10.0
- Checkstyle suppression: `@SuppressWarnings("checkstyle:illegalcatch")` on `ExceptionalSupplier.execute()` and similar
