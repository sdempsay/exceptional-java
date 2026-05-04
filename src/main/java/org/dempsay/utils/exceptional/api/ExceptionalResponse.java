package org.dempsay.utils.exceptional.api;

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
    public Optional<R> safeResponse() {
        return Optional.ofNullable(response);
    }

    /**
     * Transforms the response value if no error occurred.
     * Returns empty Optional if there was an error or if response is null.
     *
     * @param mapper function to transform the response value
     * @return Optional containing the transformed value, or empty if failed
     * @since 1.0.0
     */
    public <N> Optional<N> map(final Function<R,N> mapper) {
        return safeResponse().map(mapper);
    }

    public boolean wasError() {
        return hasError().test(this);
    }

    public boolean wasNoError() {
        return hasError().negate().test(this);
    }

    public static <R> ExceptionalResponse<R> success(final R result) {
        return new ExceptionalResponse<R>(result, false);
    }

    public static <R> ExceptionalResponse<R> failure() {
        return new ExceptionalResponse<R>(null, true);
    }

    public static Predicate<ExceptionalResponse<?>> hasError() {
        return r -> r.wasError;
    }
}
