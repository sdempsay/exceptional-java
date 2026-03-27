package org.dempsay.utils.exceptional.api;

/**
 * Used to optionally consume errors when they occur
 *
 * @since 1.0.0
 * @author Shawn Dempsay {@literal <sdempsay@pavlovmedia.com>}
 */
public interface ExceptionalListener {
    void onError(Exception error);
}
