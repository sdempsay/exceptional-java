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
    void accept(T t) throws Exception;
}
