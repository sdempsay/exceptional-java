package org.dempsay.utils.exceptional.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

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
    public void testMapSuccessNull() {
        var response = ExceptionalResponse.<String>failure();
        assertTrue(response.wasError());
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
        ExceptionalResponse<String> response = ExceptionalResponse.failure();
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

    @Test
    public void testStreamSuccess() {
        var response = ExceptionalResponse.success("Hello");
        List<String> list = response.stream().toList();

        assertEquals(1, list.size());
        assertEquals("Hello", list.get(0));
    }

    @Test
    public void testStreamFailure() {
        var response = ExceptionalResponse.<String>failure();
        List<String> list = response.stream().toList();

        assertTrue(list.isEmpty());
    }

    @Test
    public void testStreamWithNullResult() {
        ExceptionalResponse<String> response = ExceptionalResponse.failure();
        List<String> list = response.stream().toList();

        assertTrue(list.isEmpty());
    }

    @Test
    @SuppressWarnings("null")
    public void testStreamWithFlatMap() {
        List<String> inputs = List.of("a", "b", "c");
        List<String> results = inputs.stream()
            .map(s -> ExceptionalResponse.<String>success(s.toUpperCase()).stream())
            .flatMap(s -> s)
            .toList();

        assertEquals(List.of("A", "B", "C"), results);
    }
}