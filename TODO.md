# Code Review TODO

## Code Style
| ID | File | Issue |
|----|------|-------|
| 1 | ExceptionalFunction.java:28 | Missing blank line before closing brace of try block |
| 2 | ExceptionalAction.java:28 | Missing blank line before closing brace of try block |

## Test Issues
| ID | File | Issue |
|----|------|-------|
| 3 | TestExceptionSupplier.java:20,29,40 | "Suppler" misspelled in test method names (should be "Supplier") |
| 4 | TestExceptionSupplier.java:29 | testUnsuccessfulSuppler test expects null response on error, but ExceptionalResponse.failure() returns null - correct but potentially confusing expectation |
| 5 | TestExceptionAction.java:32 | testUnsuccessfulAction test has unhandled exception - could be confusing |

## API Enhancements
| ID | Item | Issue |
|----|------|-------|
| 6 | README.md | Minimal documentation - just one line "because I hate exceptions", no usage examples |

## Potential API Improvements
| ID | Item | Issue |
|----|------|-------|
| 7 | ExceptionalResponse | No helper methods for common patterns (ifSuccess/ifError, map, orElse style methods) |
