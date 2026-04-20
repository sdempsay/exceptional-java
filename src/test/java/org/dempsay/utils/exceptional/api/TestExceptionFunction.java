package org.dempsay.utils.exceptional.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicReference;

import org.dempsay.utils.exceptional.api.ExceptionalFunction;
import org.junit.jupiter.api.Test;

/**
 * Tests and samples against the ExceptionalFunction
 *
 * @since 1.0.0
 * @author Shawn Dempsay {@literal <shawn@dempsay.org>}
 */
public class TestExceptionFunction {
    @Test
    public void testSuccessfulSuppler() {
        ExceptionalFunction<String,Integer> function = ExceptionalFunction.of(Integer::parseInt);
        var response = function.execute("67");
        assertTrue(response.wasNoError(), "No errors");
        assertEquals(67, response.response(), "Got the correct response");
        assertEquals(67, response.safeResponse().get(), "Got the correct wrapped response");
    }

    @Test
    public void testUnsuccessfulSuppler() {
        ExceptionalFunction<String,Integer> function = ExceptionalFunction.of(Integer::parseInt);
        var response = function.execute("Six-Seven");
        assertTrue(response.wasError(), "Had errors");
        assertNull(response.response(), "Response is null");
        assertTrue(response.safeResponse().isEmpty(), "Got an empty response");
    }

    @Test
    public void testUnsuccessfulSupplerListener() {
        AtomicReference<Exception> caughtException = new AtomicReference<>();
        ExceptionalFunction<String,Integer> function = ExceptionalFunction.of(Integer::parseInt);
        var response = function.with(caughtException::set).execute("Six-Seven");
        assertTrue(response.wasError(), "Had errors");
        assertNull(response.response(), "Response is null");
        assertTrue(response.safeResponse().isEmpty(), "Got an empty response");
        assertTrue(caughtException.get() instanceof NumberFormatException, "Got a NumberFormatException");
        assertEquals("For input string: \"Six-Seven\"", caughtException.get().getMessage(), "Got the correct message");
    }
}
