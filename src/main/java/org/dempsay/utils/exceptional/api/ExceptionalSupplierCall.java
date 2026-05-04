package org.dempsay.utils.exceptional.api;

/**
 * Prototype for an exception function
 *
 * @since 1.0.0
 * @author Shawn Dempsay {@literal <shawn@dempsay.org>}
 */
@FunctionalInterface
public interface ExceptionalSupplierCall<R> {
    /**
     * Supplies a result or throws an exception.
     *
     * @return the result
     * @throws Exception if an error occurs
     * @since 1.0.0
     */
    R supply() throws Exception;
}
