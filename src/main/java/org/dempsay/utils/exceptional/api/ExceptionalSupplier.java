package org.dempsay.utils.exceptional.api;

import java.util.Objects;

/**
 * Utility class for executing actions
 *
 * @since 1.0.0
 * @author Shawn Dempsay {@literal <shawn@dempsay.org>}
 */
public final class ExceptionalSupplier<R> {
    private final ExceptionalSupplierCall<R> exceptionalSupplier;
    private ExceptionalListener exceptionalListener;

    private ExceptionalSupplier(final ExceptionalSupplierCall<R> exceptionalSupplier) {
        this.exceptionalSupplier = exceptionalSupplier;
    }

    /**
     * Sets an error listener to be called if an exception occurs during execution.
     *
     * @param exceptionalListener the listener to receive exceptions
     * @return this instance for method chaining
     * @since 1.0.0
     */
    public ExceptionalSupplier<R> with(final ExceptionalListener exceptionalListener) {
        this.exceptionalListener = exceptionalListener;
        return this;
    }

    /**
     * Executes the supplier, returning an ExceptionalResponse with the result or failure.
     *
     * @return ExceptionalResponse.success(result) on success, ExceptionalResponse.failure() on exception
     * @since 1.0.0
     */
    @SuppressWarnings("checkstyle:illegalcatch")
    public ExceptionalResponse<R> execute() {
        try {
            R response = this.exceptionalSupplier.supply();
            return ExceptionalResponse.success(response);
        } catch (Exception e) {
            if (Objects.nonNull(this.exceptionalListener)) {
                exceptionalListener.onError(e);
            }
            return ExceptionalResponse.failure();
        }
    }

    /**
     * Creates an ExceptionalSupplier from an ExceptionalSupplierCall.
     *
     * @param exceptionalSupplier the supplier to wrap
     * @return new ExceptionalSupplier instance
     * @since 1.0.0
     */
    public static <R> ExceptionalSupplier<R> of(final ExceptionalSupplierCall<R> exceptionalSupplier) {
        return new ExceptionalSupplier<R>(exceptionalSupplier);
    }
}
