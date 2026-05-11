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
    private Runnable always;

    private ExceptionalFunction(final ExceptionalFunctionCall<T,R> exceptionalFunction) {
        this.exceptionalFunction = exceptionalFunction;
    }

    /**
     * Sets an error listener to be called if an exception occurs during execution.
     *
     * @param exceptionalListener the listener to receive exceptions
     * @return this instance for method chaining
     * @since 1.0.0
     */
    public ExceptionalFunction<T,R> with(final ExceptionalListener exceptionalListener) {
        this.exceptionalListener = exceptionalListener;
        return this;
    }

    /**
     * Has a runnable action that always gets called, if there was an error or not.
     * this is similar to adding something to with, and doing a check for success
     * 
     * @param always
     * @return this instance for method chaining
     * @since 1.0.7
     */
    public ExceptionalFunction<T,R> always(final Runnable always) {
        this.always = always;
        return this;
    }

    /**
     * Executes the function with the given input, returning an ExceptionalResponse with the result or failure.
     *
     * @param input the input to pass to the function
     * @return ExceptionalResponse.success(result) on success, ExceptionalResponse.failure() on exception
     * @since 1.0.0
     */
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
        } finally {
            if (Objects.nonNull(this.always)) {
                this.always.run();
            }
        }
    }

    /**
     * Creates an ExceptionalFunction from an ExceptionalFunctionCall.
     *
     * @param exceptionalFunction the function to wrap
     * @return new ExceptionalFunction instance
     * @since 1.0.0
     */
    public static <T,R> ExceptionalFunction<T,R> of(final ExceptionalFunctionCall<T,R> exceptionalFunction) {
        return new ExceptionalFunction<T,R>(exceptionalFunction);
    }
}
