# Release Notes

## Version 1.1.0

### Null-Safety Annotations
- Added `org.jetbrains:annotations` dependency (provided scope) for IDE null-safety
- `@NotNull` on `ExceptionalFunction.execute()`, `ExceptionalFunctionCall.apply()`, `ExceptionalSupplier.success()` boundary points
- `@SuppressWarnings("null")` on `ExceptionalResponse.failure()`, `then()`, `ExceptionalResource.execute()` where free type variables can't prove non-null to JDT flow analysis
- `ExceptionalResponse.success()` handles null gracefully — returns failure response instead of requiring `@NotNull` annotation

---

## Version 1.0.7

### New Features
- **always() method**: Added to all Exceptional classes (`ExceptionalSupplier`, `ExceptionalFunction`, `ExceptionalAction`, `ExceptionalResource`) - executes a Runnable in a finally block regardless of success or failure, useful for guaranteed cleanup
- **chain() method**: Added to `ExceptionalResponse` for API-style chaining where each step returns an `ExceptionalResponse` directly (vs wrapping a throwing function)
- **ExceptionalListener first parameter pattern**: Implemented for chaining methods to enable varargs support in API methods

### New Functional Interfaces
- `ExceptionalChainCall<T,R>`: `(ExceptionalListener, T) -> ExceptionalResponse<R>`
- `ExceptionalChainListenenerCall<T,R>`: `(T, ExceptionalListener) -> ExceptionalResponse<R>`
- `ExceptionalChainListenenerInvertedCall<T,R>`: `(ExceptionalListener, T) -> ExceptionalResponse<R>`

### New Methods
- `ExceptionalListener.wrap(Consumer<Exception>)`: Static method to convert a Consumer<Exception> to an ExceptionalListener for cleaner API integration

---

## Version 1.0.6

### Improvements
- Fixed manifest post-BND processing

---

## Version 1.0.5

### Improvements
- Made the project OSGi compatible

---

## Version 1.0.4

### New Features
- **Consumer<Exception> support**: `ExceptionalListener` now extends `Consumer<Exception>`, allowing direct integration with APIs that expect `Consumer<Exception>` - no wrapper needed

### Documentation
- Updated `WhyBeExceptional.md` with improved documentation on Consumer<Exception> interoperability

---

## Version 1.0.3

### New Features
- **stream() method**: Added to `ExceptionalResponse` - returns a Stream containing the response if successful and non-null, otherwise empty. Enables stream-friendly filtering with flatMap
- **map() method**: Added to `ExceptionalResponse` - transforms response values while staying in the Optional world, safe for null results

### Enhancements
- Added Javadoc to all API methods across all classes

---

## Version 1.0.2

### New Features
- **then() method**: Added to `ExceptionalResponse` - chains to another potentially-failing operation (like flatMap for ExceptionalResponse). If current response has an error, short-circuits to failure

---

## Version 1.0.1

### Bug Fixes
- Fixed test package structure - moved test files to proper `org.dempsay.utils.exceptional.api` subpackage

---

## Version 1.0.0

### Initial Release

Core functionality:
- `ExceptionalAction`: Execute void actions that may throw exceptions
- `ExceptionalSupplier`: Execute suppliers (no input, returns value) that may throw exceptions
- `ExceptionalFunction`: Execute functions (accepts input, returns value) that may throw exceptions
- `ExceptionalListener`: Callback interface for error handling
- `ExceptionalResponse`: Response wrapper that encapsulates success or failure
- `ExceptionalResource`: AutoCloseable resource lifecycle management (value returning)
- `ExceptionalResourceAction`: AutoCloseable resource lifecycle management (void)

Key patterns:
- `.with(listener)` for optional error callbacks
- `.execute()` returns `ExceptionalResponse<R>` or boolean for actions
- `wasError()` / `wasNoError()` for explicit failure handling
- `safeResponse()` returns Optional for safe access