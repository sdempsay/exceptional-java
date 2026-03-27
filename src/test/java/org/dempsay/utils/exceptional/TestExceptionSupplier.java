package org.dempsay.utils.exceptional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicReference;

import org.dempsay.utils.exceptional.api.ExceptionalSupplier;
import org.junit.jupiter.api.Test;

/**
 * Tests and samples against the ExceptionalSupplier
 *
 * @since 1.0.0
 * @author Shawn Dempsay {@literal <sdempsay@pavlovmedia.com>}
 */
public class TestExceptionSupplier {
    @Test
    public void testSuccessfulSuppler() {
        var supplier = ExceptionalSupplier.of(() -> "Hello there");
        var response = supplier.execute();
        assertTrue(response.wasNoError(), "No errors");
        assertEquals("Hello there", response.response(), "Got the correct response");
        assertEquals("Hello there", response.safeResponse().get(), "Got the correct wrapped response");
    }

    @Test
    public void testUnsuccessfulSuppler() {
        var supplier = ExceptionalSupplier.of(() -> {
                throw new IllegalStateException("Exception for the test");
            });
        var response = supplier.execute();
        assertTrue(response.wasError(), "Had errors");
        assertNull(response.response(), "Response is null");
        assertTrue(response.safeResponse().isEmpty(), "Got an empty response");
    }

    @Test
    public void testUnsuccessfulSupplerListener() {
        AtomicReference<Exception> caughtException = new AtomicReference<>();
        var supplier = ExceptionalSupplier.of(() -> {
                throw new IllegalStateException("Exception for the test");
            }).with(caughtException::set);
        var response = supplier.execute();
        assertTrue(response.wasError(), "Had errors");
        assertNull(response.response(), "Response is null");
        assertTrue(response.safeResponse().isEmpty(), "Got an empty response");
        assertTrue(caughtException.get() instanceof IllegalStateException, "Got an illegal state exception");
        assertEquals("Exception for the test", caughtException.get().getMessage(), "Got the correct message");
    }
}
