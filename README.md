# Exceptional - The Java Library That Might Reduce Your Job Security

*Or: How I Learned to Stop Worrying and Love the Response Object*

---

## The Hook

You know what the best thing about exceptions in Java is? There's always *more* of them to handle!

Every Java developer has that one method that looks like this:

```java
public String processData(String input) throws IOException, SQLException, InterruptedException {
    // actual work happens somewhere in here
}
```

And then the caller has to handle all those exceptions. And *their* caller has to handle them too. Eventually you end up with a stack trace deeper than your company's onboarding documentation.

Someone, somewhere, once thought: *"Let's make developers explicitly acknowledge every possible way their code could fail!"* And thus, checked exceptions were born.

*Job security achieved. Efficiency... not so much.*

---

## The Vibe

I watched a presentation once where someone made a tongue-in-cheek argument that **exception handling is job security**. Every try-catch block is another line of code only *you* understand. Every throws clause is another dependency that keeps you employed.

And honestly? They had a point. Exception handling adds *layers* to your codebase. Layers that obscure what the code is actually trying to do.

But here's the thing: most exceptions aren't actually recoverable. The network is down. The file doesn't exist. The external service timed out. You can't *do* anything meaningful in a catch block except log and propagate.

What if we stopped pretending recovery was possible and just... acknowledged failure explicitly?

**That's what Exceptional does.**

---

## What Is This?

Exceptional is a Java library that wraps exception-throwing code in try-catch blocks and returns a response object instead of throwing.

Instead of:

```java
try {
    return fetchUser(id);
} catch (IOException e) {
    logger.error("Failed", e);
    return null;
}
```

You get:

```java
return ExceptionalSupplier.of(() -> fetchUser(id))
    .execute();
```

And it returns an `ExceptionalResponse` that tells you cleanly whether it worked or not.

---

## Quick Start

### Add Dependency

```xml
<dependency>
  <groupId>org.dempsay.utils</groupId>
  <artifactId>exceptional</artifactId>
  <version>1.0.4</version>
</dependency>
```

### Imports

```java
import org.dempsay.utils.exceptional.api.ExceptionalSupplier;
import org.dempsay.utils.exceptional.api.ExceptionalResponse;
import org.dempsay.utils.exceptional.api.ExceptionalFunction;
import org.dempsay.utils.exceptional.api.ExceptionalAction;
import org.dempsay.utils.exceptional.api.ExceptionalResource;
import org.dempsay.utils.exceptional.api.ExceptionalResourceAction;
```

### Basic Usage

```java
// Supplier (returns a value)
var response = ExceptionalSupplier.of(() -> fetchData()).execute();
if (response.wasNoError()) {
    use(response.response());
}

// Action (void operation)
boolean success = ExceptionalAction.of(() -> doSomething()).execute();

// Function (takes input, returns value)
var result = ExceptionalFunction.of(Integer::parseInt).execute("42");
```

---

## Why This Matters

### 1. Stream Operations Don't Look Like Crime Scenes

```java
// Before: try-catch everywhere, hidden logic
List<String> results = inputs.stream()
    .map(input -> {
        try {
            return processData(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    })
    .collect(Collectors.toList());

// After: clean, readable, elegant
List<String> results = inputs.stream()
    .map(input -> ExceptionalSupplier.of(() -> processData(input)).execute())
    .flatMap(ExceptionalResponse::stream)
    .toList();
```

### 2. Business Logic Stays Visible

```java
// Your code actually looks like your code
ExceptionalSupplier.of(() -> {
    Data data = fetchFromNetwork();
    Processed processed = transform(data);
    return aggregate(processed);
}).with(error -> logger.warn("Failed for input: {}", input, error))
 .execute();
```

### 3. Explicit Failure Handling

No more silent nulls or mysterious NPEs three stack frames away:

```java
var response = ExceptionalSupplier.of(() -> riskyCall()).execute();

if (response.wasNoError()) {
    return response.response();
} else {
    logger.error("Operation failed", error);
    return fallback();
}
```

### 4. Resources Handled Properly

```java
var response = ExceptionalResource.of(
    () -> new DatabaseConnection(),
    conn -> conn.query("SELECT * FROM users")
).execute();
```

Resources close automatically, even if your query throws. Because we're not animals.

---

## The Trade

Yes, using Exceptional means you're accepting a result object instead of throwing.

You're saying: *"I acknowledge this might fail, and I'll handle it explicitly."*

That's a feature, not a bug. You're trading the *illusion* of recovery for *clarity* about failure.

And if you're worried about your job security... don't be. There's still plenty of code that needs writing. You'll just have more time to write the *important* code instead of the exception handling boilerplate.

*(Also, you can always build retry logic, circuit breakers, and other resilience patterns on top of this library. That's another blog post.)*

---

## The Philosophy

Not every exception needs a try-catch. Most failures are external, transient, and unrecoverable. The best thing you can do is acknowledge them and move on.

Exceptional makes that easy. It makes failure visible. It makes your code readable.

**Now go write some code that actually matters.**

---

For more details, check out [WhyBeExceptional.md](./WhyBeExceptional.md) - the technical deep dive.