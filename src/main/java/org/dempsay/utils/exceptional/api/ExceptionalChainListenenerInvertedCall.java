package org.dempsay.utils.exceptional.api;

/**
 * Functional interface for more API like chains
 *
 * @since 1.0.7
 * @author Shawn Dempsay {@literal <shawn@dempsay.org>}
 * 
 * @param <T> Input type
 * @param <T> Return type
 */
@FunctionalInterface
public interface ExceptionalChainListenenerInvertedCall<T,R> {
    ExceptionalResponse<R> apply(ExceptionalListener listener, T t);
}
