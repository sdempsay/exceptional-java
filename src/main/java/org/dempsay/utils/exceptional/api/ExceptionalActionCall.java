package org.dempsay.utils.exceptional.api;

/**
 * Prototype for an exception action
 *
 * @since 1.0.0
 * @author Shawn Dempsay {@literal <sdempsay@pavlovmedia.com>}
 */
@FunctionalInterface
public interface ExceptionalActionCall {
    void action() throws Exception;
}
