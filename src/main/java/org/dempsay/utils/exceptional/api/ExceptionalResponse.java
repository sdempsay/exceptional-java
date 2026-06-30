package org.dempsay.utils.exceptional.api;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Wrapper for handling responses
 *
 * @since 1.0.0
 * @author Shawn Dempsay {@literal <shawn@dempsay.org>}
 */
public record ExceptionalResponse<R>(R response, boolean wasError) {
    /**
     * Returns the response as an Optional, safely handling null responses.
     *
     * @return Optional.ofNullable(response)
     * @since 1.0.0
     */
    public Optional<R> safeResponse() {
        return Optional.ofNullable(response);
    }

    /**
     * Transforms the response value if no error occurred.
     * Returns empty Optional if there was an error or if response is null.
     *
     * @param mapper function to transform the response value
     * @return Optional containing the transformed value, or empty if failed
     * @since 1.0.3
     */
    public <N> Optional<N> map(final Function<R,N> mapper) {
        return safeResponse().map(mapper);
    }

    /**
     * Chains to another potentially-failing function.
     * If this response had an error, returns failure without executing next.
     * If successful, executes the next function with this response's value.
     *
     * @param next the function to execute if this response has no error
     * @param exceptionalListener optional listener for errors in the next function
     * @return new ExceptionalResponse with the result of next, or failure if any step failed
     * @since 1.0.3
     */
    @SuppressWarnings("null")
    public <N> ExceptionalResponse<N> then(final ExceptionalFunctionCall<R,N> next, final ExceptionalListener exceptionalListener) {
        if (this.wasError || Objects.isNull(this.response)) {
            return ExceptionalResponse.failure();
        } else {
            var nextCall = ExceptionalFunction.of(next);
            if (Objects.nonNull(exceptionalListener)) {
                nextCall.with(exceptionalListener);
            }
            return nextCall.execute(this.response);
        }
    }

    /**
     * Chains to another potentially-failing function without an error listener.
     *
     * @param next the function to execute if this response has no error
     * @return new ExceptionalResponse with the result of next, or failure if any step failed
     * @since 1.0.3
     */
    public <N> ExceptionalResponse<N> then(final ExceptionalFunctionCall<R,N> next) {
        return this.wasError || Objects.isNull(this.response)
            ? ExceptionalResponse.failure()
            : then(next, null);
    }

    /**
     * Chains the response to a function call that returns an ExceptionalResponse
     * versus wrapping an unsafe call. Useful in API contexts
     * 
     * @param <N> The next return type
     * @param next the function to execute if this response has no error
     * @param exceptionalListener optional listener for errors in the next function
     * @return new ExceptionalResponse with the result of next, or failure if any step failed
     * @since 1.0.7
     */
    public <N> ExceptionalResponse<N> chain(final ExceptionalChainCall<R,N> next, final ExceptionalListener exceptionalListener) {
        return chain(exceptionalListener, next);
    }

    public <N> ExceptionalResponse<N> chain(final ExceptionalListener exceptionalListener, final ExceptionalChainCall<R,N> next) {
        if (this.wasError) {
            return ExceptionalResponse.failure();
        }
        return next.apply(exceptionalListener, this.response);
    }

    /**
     * Chains the response to a function call that returns an ExceptionalResponse
     * versus wrapping an unsafe call. Useful in API contexts
     * 
     * @param <N> The next return type
     * @param next the function to execute if this response has no error
     * @return new ExceptionalResponse with the result of next, or failure if any step failed
     * @since 1.0.7
     */
    public <N> ExceptionalResponse<N> chain(final ExceptionalChainCall<R,N> next) {
        return chain(next, null);
    }

    /**
     * Chains the response to a function call that accepts an ExceptionalListener and returns 
     * an ExceptionalResponseversus wrapping an unsafe call. Useful in API contexts
     * 
     * @param <N> The next return type
     * @param next the function to execute if this response has no error
     * @param exceptionalListener
     * @return new ExceptionalResponse with the result of next, or failure if any step failed
     * @since 1.0.7
     */
    public <N> ExceptionalResponse<N> chain(final ExceptionalListener exceptionalListener, final ExceptionalChainListenenerCall<R,N> next) {
        if (this.wasError) {
            return ExceptionalResponse.failure();
        }
        return next.apply(this.response, exceptionalListener);
    }

    /**
     * Chains the response to a function call that accepts an ExceptionalListener and returns 
     * an ExceptionalResponseversus wrapping an unsafe call. Useful in API contexts.<p>
     * <strong>NOTE</strong>: This is an inverted call to handle updated API semantics
     * 
     * 
     * @param <N> The next return type
     * @param exceptionalListener
     * @param next the function to execute if this response has no error
     * @return new ExceptionalResponse with the result of next, or failure if any step failed
     * @since 1.0.7
     */
    public <N> ExceptionalResponse<N> chain(final ExceptionalListener exceptionalListener, final ExceptionalChainListenenerInvertedCall<R,N> next) {
        if (this.wasError) {
            return ExceptionalResponse.failure();
        }
        return next.apply(exceptionalListener, this.response);
    }

    /**
     * Returns true if an error occurred during execution.
     *
     * @return true if wasError is true
     * @since 1.0.0
     */
    public boolean wasError() {
        return hasError().test(this);
    }

    /**
     * Returns true if no error occurred during execution.
     *
     * @return true if wasError is false
     * @since 1.0.0
     */
    public boolean wasNoError() {
        return hasError().negate().test(this);
    }

    /**
     * Creates a successful response with the given result.
     *
     * @param result the successful result (must be non-null)
     * @return ExceptionalResponse with wasError = false
     * @since 1.0.0
     */
    public static <R> ExceptionalResponse<R> success(final R result) {
        return Objects.nonNull(result)
            ? new ExceptionalResponse<R>(result, false)
            : new ExceptionalResponse<R>(result, true);
      }

    /**
     * Creates a failure response.
     *
     * @return ExceptionalResponse with null response and wasError = true
     * @since 1.0.0
     */
    @SuppressWarnings("null")
    public static <R> ExceptionalResponse<R> failure() {
        return new ExceptionalResponse<R>(null, true);
    }

    /**
     * Returns a predicate that tests if an ExceptionalResponse has an error.
     *
     * @return Predicate that tests the wasError field
     * @since 1.0.0
     */
    public static Predicate<ExceptionalResponse<?>> hasError() {
        return r -> r.wasError;
    }

    /**
     * Returns the response as a Stream, suitable for use with Stream#flatMap.
     * Returns an empty stream if there was an error or if response is null.
     * This enables filtering out failures in stream pipelines:
     *
     * <pre>{@code
     * List<Result> results = inputs.stream()
     *     .map(input -> ExceptionalSupplier.of(() -> process(input)).execute())
     *     .flatMap(ExceptionalResponse::stream)
     *     .toList();
     * }</pre>
     *
     * @return Stream containing the response if successful and non-null, otherwise empty
     * @since 1.0.3
     */
    public Stream<R> stream() {
        return safeResponse().map(Stream::of).orElseGet(Stream::empty);
    }
}
