package org.dempsay.utils.exceptional.api;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Wrapper for handling responses
 *
 * @since 1.0.0
 * @author Shawn Dempsay {@literal <sdempsay@pavlovmedia.com>}
 */
public record ExceptionalResponse<R>(R response, boolean wasError) {
    public Optional<R> safeResponse() {
        return Optional.ofNullable(response);
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
