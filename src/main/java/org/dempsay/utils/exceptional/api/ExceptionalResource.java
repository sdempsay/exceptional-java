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

    public ExceptionalResource<T, R> with(final ExceptionalListener exceptionalListener) {
        this.exceptionalListener = exceptionalListener;
        return this;
    }

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

    public static <T extends AutoCloseable, R> ExceptionalResource<T, R> of(
            final ExceptionalSupplierCall<T> resourceSupplier, 
            final ExceptionalFunctionCall<T, R> resourceUsage) {
        return new ExceptionalResource<>(resourceSupplier, resourceUsage);
    }
}
