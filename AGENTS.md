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

## Versioning (`@since` and SNAPSHOT)

SNAPSHOT minor is one ahead of the release line. This repo is `1.1.0-SNAPSHOT` (tracks `1.0.x`). Never use the POM SNAPSHOT as `@since`. Test classes do not need `@since`.

1. `git tag --sort=-v:refname | head -5`
2. POM e.g. `1.1.0-SNAPSHOT` → release line `1.0`
3. Latest tag on that line (e.g. `1.0.12`)
4. `@since` = that tag + 1 patch (`1.0.13`)
5. No tags on the line yet → `1.(x-1).0`

Current: latest `1.0` tag `1.0.9` → `@since 1.0.10`.

## Notes
- Parent POM: `org.dempsay.maven:dempsay-parent:1.0.4`
- JUnit version: 5.10.0
- Checkstyle suppression: `@SuppressWarnings("checkstyle:illegalcatch")` on `ExceptionalSupplier.execute()` and similar
