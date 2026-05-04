package org.dempsay.utils.exceptional.api;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

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
    public <N> ExceptionalResponse<N> then(final ExceptionalFunctionCall<R,N> next, final ExceptionalListener exceptionalListener) {
        if (this.wasError) {
            return ExceptionalResponse.failure();
        }

        var nextCall = ExceptionalFunction.of(next);
        if (Objects.nonNull(exceptionalListener)) {
            nextCall.with(exceptionalListener);
        }

        return nextCall.execute(this.response);
    }

    /**
     * Chains to another potentially-failing function without an error listener.
     *
     * @param next the function to execute if this response has no error
     * @return new ExceptionalResponse with the result of next, or failure if any step failed
     * @since 1.0.3
     */
    public <N> ExceptionalResponse<N> then(final ExceptionalFunctionCall<R,N> next) {
        return then(next, null);
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
     * @param result the successful result
     * @return ExceptionalResponse with wasError = false
     * @since 1.0.0
     */
    public static <R> ExceptionalResponse<R> success(final R result) {
        return new ExceptionalResponse<R>(result, false);
    }

    /**
     * Creates a failure response.
     *
     * @return ExceptionalResponse with null response and wasError = true
     * @since 1.0.0
     */
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
}
