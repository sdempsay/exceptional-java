package org.dempsay.utils.exceptional.api;

/**
 * Used to optionally consume errors when they occur
 *
 * @since 1.0.0
 * @author Shawn Dempsay {@literal <shawn@dempsay.org>}
 */
public interface ExceptionalListener {
    void onError(Exception error);
}
