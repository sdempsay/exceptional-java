package org.dempsay.utils.exceptional.api;

import org.jetbrains.annotations.NotNull;

/**
 * Prototype for an exception function
 *
 * @since 1.0.0
 * @author Shawn Dempsay {@literal <shawn@dempsay.org>}
 */
@FunctionalInterface
public interface ExceptionalFunctionCall<T,R> {
    /**
     * Applies the function to the input or throws an exception.
     *
     * @param t the input
     * @return the result
     * @throws Exception if an error occurs
     * @since 1.0.0
     */
    R apply(@NotNull T t) throws Exception;
}
