package org.dempsay.utils.exceptional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicReference;

import org.dempsay.utils.exceptional.api.ExceptionalAction;
import org.junit.jupiter.api.Test;

/**
 * Tests and samples against the ExceptionalAction
 *
 * @since 1.0.0
 * @author Shawn Dempsay {@literal <shawn@dempsay.org>}
 */
public class TestExceptionAction {
    @Test
    public void testSuccessfulAction() {
        var action = ExceptionalAction.of(() -> System.out.println("Hello World"));
        assertTrue(action.execute(), "Action executed correctly");
    }

    @Test
    public void testUnsuccessfulAction() {
        var action = ExceptionalAction.of(() -> {
            throw new IllegalStateException("Error for this test");
        });
        assertFalse(action.execute(), "Action executed incorrectly");
    }

    @Test
    public void testUnsuccessfulActionListener() {
        AtomicReference<Exception> caughtException = new AtomicReference<>();
        var action = ExceptionalAction.of(() -> {
            throw new IllegalStateException("Error for this test");
        }).with(caughtException::set);

        assertFalse(action.execute(), "Action executed incorrectly");
        assertNotNull(caughtException.get(), "Got an exception");
        assertTrue(caughtException.get() instanceof IllegalStateException, "Got an illegal state exception");
        assertEquals("Error for this test", caughtException.get().getMessage(), "Got the correct message");
    }
}
