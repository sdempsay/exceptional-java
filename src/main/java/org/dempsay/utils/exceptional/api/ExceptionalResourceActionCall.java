package org.dempsay.utils.exceptional.api;

/**
 * Prototype for an exception resource action
 *
 * @since 1.0.0
 * @author Shawn Dempsay {@literal <shawn@dempsay.org>}
 * @param <T> the type of the resource
 */
@FunctionalInterface
public interface ExceptionalResourceActionCall<T> {
    /**
     * Accepts the resource and performs an action, or throws an exception.
     *
     * @param t the resource
     * @throws Exception if an error occurs
     * @since 1.0.0
     */
    void accept(T t) throws Exception;
}
