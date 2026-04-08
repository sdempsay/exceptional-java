package org.dempsay.utils.exceptional.api;

/**
 * Prototype for an exception action
 *
 * @since 1.0.0
 * @author Shawn Dempsay {@literal <shawn@dempsay.org>}
 */
@FunctionalInterface
public interface ExceptionalActionCall {
    void action() throws Exception;
}
