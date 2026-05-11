package org.dempsay.utils.exceptional.api;

/**
 * Functional interface for more API like chains
 *
 * @since 1.0.7
 * @author Shawn Dempsay {@literal <shawn@dempsay.org>}
 * 
 * @param <T> Input type
 * @param <R> Return type
 */
@FunctionalInterface
public interface ExceptionalChainListenenerCall<T,R> {
    ExceptionalResponse<R> apply(T t, ExceptionalListener listener);
}
