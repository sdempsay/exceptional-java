package org.dempsay.utils.exceptional.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.dempsay.utils.exceptional.api.ExceptionalResource;
import org.dempsay.utils.exceptional.api.ExceptionalResourceAction;
import org.dempsay.utils.exceptional.api.ExceptionalResponse;
import org.junit.jupiter.api.Test;

/**
 * Tests and samples against the ExceptionalResource and ExceptionalResourceAction
 *
 * @since 1.0.0
 * @author Shawn Dempsay {@literal <shawn@dempsay.org>}
 */
public class TestExceptionResource {

    private static class MockResource implements AutoCloseable {
        private final AtomicBoolean closed = new AtomicBoolean(false);
        private final boolean failOnClose;

        MockResource(boolean failOnClose) {
            this.failOnClose = failOnClose;
        }

        String doWork() {
            return "Work done";
        }

        void doWorkVoid() {
            // No-op
        }

        @Override
        public void close() throws Exception {
            if (failOnClose) {
                throw new IOException("Close failed");
            }
            closed.set(true);
        }

        boolean isClosed() {
            return closed.get();
        }
    }

    @Test
    public void testExceptionalResourceSuccess() {
        var response = ExceptionalResource.<MockResource, String>of(
            () -> new MockResource(false),
            res -> res.doWork()
        ).execute();
        
        assertTrue(response.wasNoError());
        assertEquals("Work done", response.response());
    }

    @Test
    public void testExceptionalResourceFailureCreation() {
        var response = ExceptionalResource.<MockResource, String>of(
            () -> { throw new IllegalStateException("Creation failed"); },
            res -> res.doWork()
        ).execute();

        assertTrue(response.wasError());
        assertNull(response.response());
    }

    @Test
    public void testExceptionalResourceFailureUsage() {
        var response = ExceptionalResource.<MockResource, String>of(
            () -> new MockResource(false),
            res -> { throw new IllegalStateException("Usage failed"); }
        ).execute();

        assertTrue(response.wasError());
        assertNull(response.response());
    }

    @Test
    public void testExceptionalResourceFailureClose() {
        var response = ExceptionalResource.<MockResource, String>of(
            () -> new MockResource(true),
            res -> res.doWork()
        ).execute();

        assertTrue(response.wasError());
        assertNull(response.response());
    }

    @Test
    public void testExceptionalResourceListener() {
        AtomicReference<Exception> caught = new AtomicReference<>();
        var response = ExceptionalResource.<MockResource, String>of(
            () -> new MockResource(false),
            res -> { throw new IllegalStateException("Usage failed"); }
        ).with(caught::set).execute();

        assertTrue(response.wasError());
        assertTrue(caught.get() instanceof IllegalStateException);
    }

    @Test
    public void testExceptionalResourceActionSuccess() {
        var success = ExceptionalResourceAction.<MockResource>of(
            () -> new MockResource(false),
            res -> res.doWorkVoid()
        ).execute();
        
        assertTrue(success);
    }

    @Test
    public void testExceptionalResourceActionFailureCreation() {
        var success = ExceptionalResourceAction.<MockResource>of(
            () -> { throw new IllegalStateException("Creation failed"); },
            res -> res.doWorkVoid()
        ).execute();

        assertTrue(!success);
    }

    @Test
    public void testExceptionalResourceActionFailureUsage() {
        var success = ExceptionalResourceAction.<MockResource>of(
            () -> new MockResource(false),
            res -> { throw new IllegalStateException("Usage failed"); }
        ).execute();

        assertTrue(!success);
    }

    @Test
    public void testExceptionalResourceActionFailureClose() {
        var success = ExceptionalResourceAction.<MockResource>of(
            () -> new MockResource(true),
            res -> res.doWorkVoid()
        ).execute();

        assertTrue(!success);
    }

    @Test
    public void testExceptionalResourceActionListener() {
        AtomicReference<Exception> caught = new AtomicReference<>();
        var success = ExceptionalResourceAction.<MockResource>of(
            () -> new MockResource(false),
            res -> { throw new IllegalStateException("Usage failed"); }
        ).with(caught::set).execute();

        assertTrue(!success);
        assertTrue(caught.get() instanceof IllegalStateException);
    }
}
