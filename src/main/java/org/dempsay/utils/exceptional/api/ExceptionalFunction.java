package org.dempsay.utils.exceptional.api;

import java.util.Objects;

/**
 * Utility class for executing functions
 *
 * @since 1.0.0
 * @author Shawn Dempsay {@literal <shawn@dempsay.org>}
 */
public final class ExceptionalFunction<T,R> {
    private final ExceptionalFunctionCall<T,R> exceptionalFunction;
    private ExceptionalListener exceptionalListener;

    private ExceptionalFunction(final ExceptionalFunctionCall<T,R> exceptionalFunction) {
        this.exceptionalFunction = exceptionalFunction;
    }

    public ExceptionalFunction<T,R> with(final ExceptionalListener exceptionalListener) {
        this.exceptionalListener = exceptionalListener;
        return this;
    }

    @SuppressWarnings("checkstyle:illegalcatch")
    public ExceptionalResponse<R> execute(final T input) {
        try {
            R response = this.exceptionalFunction.apply(input);
            return ExceptionalResponse.success(response);
        } catch (Exception e) {
            if (Objects.nonNull(this.exceptionalListener)) {
                exceptionalListener.onError(e);
            }
            return ExceptionalResponse.failure();
        }
    }

    public static <T,R> ExceptionalFunction<T,R> of(final ExceptionalFunctionCall<T,R> exceptionalFunction) {
        return new ExceptionalFunction<T,R>(exceptionalFunction);
    }
}
