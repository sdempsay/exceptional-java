package org.dempsay.utils.exceptional.api;

/**
 * Used to optionally consume errors when they occur
 *
 * @since 1.0.0
 * @author Shawn Dempsay {@literal <shawn@dempsay.org>}
 */
public interface ExceptionalListener {
    /**
     * Called when an exception occurs during execution.
     *
     * @param error the exception that was thrown
     * @since 1.0.0
     */
    void onError(Exception error);
}
