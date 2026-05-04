package org.dempsay.utils.exceptional.api;

import java.util.Objects;

/**
 * Utility class for executing resource-based operations that return a value.
 *
 * @since 1.0.0
 * @author Shawn Dempsay {@literal <shawn@dempsay.org>}
 * @param <T> the type of the resource
 * @param <R> the type of the result
 */
public final class ExceptionalResource<T extends AutoCloseable, R> {
    private final ExceptionalSupplierCall<T> resourceSupplier;
    private final ExceptionalFunctionCall<T, R> resourceUsage;
    private ExceptionalListener exceptionalListener;

    private ExceptionalResource(final ExceptionalSupplierCall<T> resourceSupplier, 
                                final ExceptionalFunctionCall<T, R> resourceUsage) {
        this.resourceSupplier = resourceSupplier;
        this.resourceUsage = resourceUsage;
    }

    /**
     * Sets an error listener to be called if an exception occurs during resource creation, usage, or closing.
     *
     * @param exceptionalListener the listener to receive exceptions
     * @return this instance for method chaining
     * @since 1.0.0
     */
    public ExceptionalResource<T, R> with(final ExceptionalListener exceptionalListener) {
        this.exceptionalListener = exceptionalListener;
        return this;
    }

    /**
     * Executes the resource operation, returning an ExceptionalResponse with the result or failure.
     * The resource is automatically closed after use, even if an exception occurs.
     *
     * @return ExceptionalResponse.success(result) on success, ExceptionalResponse.failure() on exception
     * @since 1.0.0
     */
    @SuppressWarnings("checkstyle:illegalcatch")
    public ExceptionalResponse<R> execute() {
        try (T resource = this.resourceSupplier.supply()) {
            R result = this.resourceUsage.apply(resource);
            return ExceptionalResponse.success(result);
        } catch (Exception e) {
            if (Objects.nonNull(this.exceptionalListener)) {
                this.exceptionalListener.onError(e);
            }
            return ExceptionalResponse.failure();
        }
    }

    /**
     * Creates an ExceptionalResource from a resource supplier and a usage function.
     *
     * @param resourceSupplier creates the resource
     * @param resourceUsage consumes the resource and returns a result
     * @return new ExceptionalResource instance
     * @since 1.0.0
     */
    public static <T extends AutoCloseable, R> ExceptionalResource<T, R> of(
            final ExceptionalSupplierCall<T> resourceSupplier, 
            final ExceptionalFunctionCall<T, R> resourceUsage) {
        return new ExceptionalResource<>(resourceSupplier, resourceUsage);
    }
}
