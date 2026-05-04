package org.dempsay.utils.exceptional.api;

import java.util.Objects;

/**
 * Utility class for executing resource-based operations that do not return a value.
 *
 * @since 1.0.0
 * @author Shawn Dempsay {@literal <shawn@dempsay.org>}
 * @param <T> the type of the resource
 */
public final class ExceptionalResourceAction<T extends AutoCloseable> {
    private final ExceptionalSupplierCall<T> resourceSupplier;
    private final ExceptionalResourceActionCall<T> resourceUsage;
    private ExceptionalListener exceptionalListener;

    private ExceptionalResourceAction(final ExceptionalSupplierCall<T> resourceSupplier, 
                                      final ExceptionalResourceActionCall<T> resourceUsage) {
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
    public ExceptionalResourceAction<T> with(final ExceptionalListener exceptionalListener) {
        this.exceptionalListener = exceptionalListener;
        return this;
    }

    /**
     * Executes the resource action, returning true on success or false if an exception occurred.
     * The resource is automatically closed after use, even if an exception occurs.
     *
     * @return true if the operation completed successfully, false if an exception was thrown
     * @since 1.0.0
     */
    @SuppressWarnings("checkstyle:illegalcatch")
    public boolean execute() {
        try (T resource = this.resourceSupplier.supply()) {
            this.resourceUsage.accept(resource);
            return true;
        } catch (Exception e) {
            if (Objects.nonNull(this.exceptionalListener)) {
                this.exceptionalListener.onError(e);
            }
            return false;
        }
    }

    /**
     * Creates an ExceptionalResourceAction from a resource supplier and a usage action.
     *
     * @param resourceSupplier creates the resource
     * @param resourceUsage consumes the resource
     * @return new ExceptionalResourceAction instance
     * @since 1.0.0
     */
    public static <T extends AutoCloseable> ExceptionalResourceAction<T> of(
            final ExceptionalSupplierCall<T> resourceSupplier, 
            final ExceptionalResourceActionCall<T> resourceUsage) {
        return new ExceptionalResourceAction<>(resourceSupplier, resourceUsage);
    }
}
