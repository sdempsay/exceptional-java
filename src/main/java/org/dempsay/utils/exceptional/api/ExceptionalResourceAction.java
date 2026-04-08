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

    public ExceptionalResourceAction<T> with(final ExceptionalListener exceptionalListener) {
        this.exceptionalListener = exceptionalListener;
        return this;
    }

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

    public static <T extends AutoCloseable> ExceptionalResourceAction<T> of(
            final ExceptionalSupplierCall<T> resourceSupplier, 
            final ExceptionalResourceActionCall<T> resourceUsage) {
        return new ExceptionalResourceAction<>(resourceSupplier, resourceUsage);
    }
}
