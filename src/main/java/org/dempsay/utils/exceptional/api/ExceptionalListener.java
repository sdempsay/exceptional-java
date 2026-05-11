package org.dempsay.utils.exceptional.api;

import java.util.function.Consumer;

/**
 * Used to optionally consume errors when they occur
 *
 * @since 1.0.0
 * @author Shawn Dempsay {@literal <shawn@dempsay.org>}
 */
public interface ExceptionalListener extends Consumer<Exception> {
    /**
     * Called when an exception occurs during execution.
     *
     * @param error the exception that was thrown
     * @since 1.0.0
     */
    void onError(Exception error);

    @Override
    /** 
     * In a lot of places where exceptional patterns are used, the
     * existing pattern is to implement Consumer<Exception>, this is
     * here so that interface can be exposed from ExceptionalListener
     * making integrations easier.
     * 
     * @param error the exception that was thrown
     * @since 1.0.4
     */
    default void accept(final Exception error) {
        this.onError(error);
    }

    /**
     * In legacy APIs we have a lot of calls that pass Consumer<Exception>. To help
     * make this more readable as APIs are updated, we can use this simple wrapper call
     * to help deveopers quickly update this functionality
     * 
     * @param onError
     * @return a new {@link ExceptionalListener} that hands everything to onError
     * @since 1.0.7
     */
    static ExceptionalListener wrap(final Consumer<Exception> onError) {
        return onError::accept;
    }
}
