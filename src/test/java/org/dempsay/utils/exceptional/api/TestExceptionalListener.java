package org.dempsay.utils.exceptional.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

/**
 * Tests and samples against the ExceptionalListener
 *
 * @since 1.0.4
 * @author Shawn Dempsay {@literal <shawn@dempsay.org>}
 */
public class TestExceptionalListener {

    @Test
    public void testListenerOnError() {
        AtomicReference<Exception> caughtException = new AtomicReference<>();
        var listener = new ExceptionalListener() {
            @Override
            public void onError(final Exception error) {
                caughtException.set(error);
            }
        };

        var exception = new IllegalArgumentException("Test error");
        listener.onError(exception);

        assertEquals(exception, caughtException.get(), "Exception was passed to onError");
    }

    @Test
    public void testListenerAcceptMethod() {
        AtomicReference<Exception> caughtException = new AtomicReference<>();
        var listener = new ExceptionalListener() {
            @Override
            public void onError(final Exception error) {
                caughtException.set(error);
            }
        };

        var exception = new IllegalArgumentException("Test error via accept");
        listener.accept(exception);

        assertEquals(exception, caughtException.get(), "Exception was passed via accept");
    }

    @Test
    public void testListenerAcceptMatchesOnError() {
        AtomicReference<String> methodCalled = new AtomicReference<>();
        var listener = new ExceptionalListener() {
            @Override
            public void onError(final Exception error) {
                methodCalled.set("onError");
            }
        };

        var exception = new RuntimeException("Test");
        listener.accept(exception);

        assertEquals("onError", methodCalled.get(), "accept() delegates to onError()");
    }

    @Test
    public void testListenerWithConsumerLambda() {
        AtomicReference<Exception> caughtException = new AtomicReference<>();
        var listener = (Consumer<Exception>) error -> caughtException.set(error);

        var exception = new IllegalStateException("Consumer lambda test");
        listener.accept(exception);

        assertInstanceOf(IllegalStateException.class, caughtException.get(), "Consumer lambda received the exception");
        assertEquals("Consumer lambda test", caughtException.get().getMessage(), "Got correct message");
    }

    }