package org.dempsay.utils.exceptional.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.dempsay.utils.exceptional.api.ExceptionalResponse;
import org.junit.jupiter.api.Test;

/**
 * Tests for ExceptionalResponse
 *
 * @since 1.0.0
 * @author Shawn Dempsay {@literal <shawn@dempsay.org>}
 */
public class TestExceptionalResponse {
    @Test
    public void testMapSuccess() {
        var response = ExceptionalResponse.success("Hello");
        Optional<String> mapped = response.map(s -> s.toUpperCase());

        assertTrue(mapped.isPresent());
        assertEquals("HELLO", mapped.get());
    }

    @Test
    public void testMapFailure() {
        var response = ExceptionalResponse.<String>failure();
        Optional<String> mapped = response.map(s -> s.toUpperCase());

        assertTrue(mapped.isEmpty());
    }

    @Test
    public void testMapTransformType() {
        var response = ExceptionalResponse.success(42);
        Optional<String> mapped = response.map(i -> "Number: " + i);

        assertTrue(mapped.isPresent());
        assertEquals("Number: 42", mapped.get());
    }

    @Test
    public void testMapWithNullResult() {
        ExceptionalResponse<String> response = ExceptionalResponse.success(null);
        Optional<String> mapped = response.map(s -> s.toUpperCase());

        assertTrue(mapped.isEmpty());
    }

    @Test
    public void testThenSuccess() {
        var response = ExceptionalResponse.success("Hello");
        var result = response.then(input -> input.toUpperCase());

        assertTrue(result.wasNoError());
        assertEquals("HELLO", result.response());
    }

    @Test
    public void testThenFailure() {
        var response = ExceptionalResponse.<String>failure();
        var result = response.then(input -> input.toUpperCase());

        assertTrue(result.wasError());
        assertNull(result.response());
    }

    @Test
    public void testThenWithListener() {
        AtomicReference<Exception> caught = new AtomicReference<>();
        var response = ExceptionalResponse.success("Hello");
        var result = response.then(input -> {
            throw new IllegalStateException("Test exception");
        }, caught::set);

        assertTrue(result.wasError());
        assertTrue(caught.get() instanceof IllegalStateException);
    }

    @Test
    public void testThenChaining() {
        var result = ExceptionalResponse.success("hello")
            .then(String::toUpperCase)
            .then(s -> s + " world");

        assertTrue(result.wasNoError());
        assertEquals("HELLO world", result.response());
    }

    @Test
    public void testThenChainingFailureStops() {
        var result = ExceptionalResponse.success("hello")
            .then(input -> {
                throw new IllegalStateException("Failed");
            })
            .then(s -> s + " world");

        assertTrue(result.wasError());
        assertNull(result.response());
    }
}