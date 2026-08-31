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

## Versioning (from git tags)

Read the **latest tag**, not the SNAPSHOT in the POM.

- **Maven SNAPSHOT** after tag `x.y.z` is `x.(y+1).0-SNAPSHOT` (always ends in `.0`).
- **Javadoc `@since`** for new APIs is `x.y.(z+1)` from that same tag.

Example: tag `1.0.9` → POM `1.1.0-SNAPSHOT`, new `@since 1.0.10`.

## Notes
- Parent POM: `org.dempsay.maven:dempsay-parent:1.0.4`
- JUnit version: 5.10.0
- Checkstyle suppression: `@SuppressWarnings("checkstyle:illegalcatch")` on `ExceptionalSupplier.execute()` and similar
