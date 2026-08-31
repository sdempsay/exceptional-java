# Actions Log

- 2026-08-31: Axiom catalog (`external_failure`): `src/main/resources/agent-catalog/catalog.yaml` plus primary `of`+`execute` and secondary `chain` snippets. `axiom-maven-plugin` bound with `required=true`. After `mvn deploy`, POST the GAV with `bin/post-catalog.sh` (or `axiom-mcp catalog add g:a:v`) to a reachable axiom-mcp. Latest tag is `1.0.9` so this SNAPSHOT stays `1.1.0-SNAPSHOT`; new `@since` would be `1.0.10`.
- 2026-06-30: Added JetBrains `@NotNull`/`@Nullable` annotations (provided scope) for null-safety. `ExceptionalResponse.success()` handles null inline (returns failure) instead of `@NotNull`. `@SuppressWarnings("null")` on `ExceptionalResource.execute()` for free type variable issue with `@NotNull` on `ExceptionalFunctionCall.apply()`.
- 2026-05-11: Added `always()` method to all Exceptional classes (`ExceptionalSupplier`, `ExceptionalFunction`, `ExceptionalAction`, `ExceptionalResource`) - runs a Runnable in finally block regardless of success/failure, added unit tests
- 2026-05-11: Added `chain()` method to `ExceptionalResponse` for API-style chaining where each step returns ExceptionalResponse directly, added functional interfaces `ExceptionalChainCall`, `ExceptionalChainListenenerCall`, `ExceptionalChainListenenerInvertedCall`
- 2026-05-11: Implemented ExceptionalListener first parameter pattern for chaining methods to enable varargs support in API methods
- 2026-05-04: Added `stream()` method to `ExceptionalResponse` for stream-friendly filtering, with 4 unit tests, javadoc, and documentation using .toList()
- 2026-05-04: Removed local checkstyle.xml - parent 1.0.4 provides checkstyle configuration
- 2026-05-04: Added Javadoc to all API methods across all classes in the exceptional framework
- 2026-05-04: Added `then()` method to `ExceptionalResponse` for chaining potentially-failing operations, with 5 unit tests, javadoc, and documentation
- 2026-05-04: Added `map()` method to `ExceptionalResponse` for transforming response values, added unit tests, javadoc, and documentation in WhyBeExceptional.md
- 2026-04-20: Fixed test package structure - moved test files from `org.dempsay.utils.exceptional` to `org.dempsay.utils.exceptional.api` package to match main source structure
- 2026-04-08: Ran `mvn verify` - build successful, all 9 tests pass
- 2026-04-08: Performed full code review of codebase
- 2026-04-08: Created TODO.md with 7 review items covering code style, tests, and API improvements
- 2026-04-08: Created ACTIONS.md with action log
- 2026-04-08: Committed all changes to git
