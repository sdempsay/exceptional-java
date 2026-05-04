package org.dempsay.utils.exceptional.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

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
}