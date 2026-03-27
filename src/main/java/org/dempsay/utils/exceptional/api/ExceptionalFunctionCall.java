package org.dempsay.utils.exceptional.api;

/**
 * Prototype for an exception function
 *
 * @since 1.0.0
 * @author Shawn Dempsay {@literal <sdempsay@pavlovmedia.com>}
 */
@FunctionalInterface
public interface ExceptionalFunctionCall<T,R> {
    R apply(T t) throws Exception;
}
