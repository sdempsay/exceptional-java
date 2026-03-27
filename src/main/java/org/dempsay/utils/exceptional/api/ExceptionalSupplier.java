package org.dempsay.utils.exceptional.api;

import java.util.Objects;

/**
 * Utility class for executing actions
 *
 * @since 1.0.0
 * @author Shawn Dempsay {@literal <sdempsay@pavlovmedia.com>}
 */
public final class ExceptionalSupplier<R> {
    private final ExceptionalSupplierCall<R> exceptionalSupplier;
    private ExceptionalListener exceptionalListener;

    private ExceptionalSupplier(final ExceptionalSupplierCall<R> exceptionalSupplier) {
        this.exceptionalSupplier = exceptionalSupplier;
    }

    public ExceptionalSupplier<R> with(final ExceptionalListener exceptionalListener) {
        this.exceptionalListener = exceptionalListener;
        return this;
    }

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

    public static <R> ExceptionalSupplier<R> of(final ExceptionalSupplierCall<R> exceptionalSupplier) {
        return new ExceptionalSupplier<R>(exceptionalSupplier);
    }
}
