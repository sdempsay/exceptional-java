package org.dempsay.utils.exceptional.api;

/**
 * Prototype for an exception action
 *
 * @since 1.0.0
 * @author Shawn Dempsay {@literal <shawn@dempsay.org>}
 */
@FunctionalInterface
public interface ExceptionalActionCall {
    /**
     * Performs an action or throws an exception.
     *
     * @throws Exception if an error occurs
     * @since 1.0.0
     */
    void action() throws Exception;
}
