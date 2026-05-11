package org.dempsay.utils.exceptional.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class TestChainingActions {
    @Test
    public void testChainingSuccess() {
        ExceptionalResponse<Integer> response = ExceptionalSupplier.of(() -> "1")
            .execute()
            .then(s -> Integer.parseInt(s));
        assertTrue(response.wasNoError(), "Did not have an error");
        assertEquals(1, response.response());
    }

    @Test
    public void testChainingSuccessAlt() {
        ArrayList<Exception> errors = new ArrayList<>();
        ExceptionalResponse<Integer> response = ExceptionalSupplier.of(() -> "1")
            .with(errors::add)
            .execute()
            .chain(this::convertString, errors::add);
        assertTrue(response.wasNoError(), "Did not have an error");
        assertEquals(1, response.response());
    }

    @Test
    public void testChainingThenFailure() {
        ArrayList<Exception> errors = new ArrayList<>();
        ExceptionalResponse<Integer> response = ExceptionalSupplier.of(() -> "One")
            .with(errors::add)
            .execute()
            .then(Integer::parseInt, errors::add);
        assertTrue(response.wasError(), "There was an error");
        assertEquals(1, errors.size(), "Got back one exception");
        assertTrue(errors.getFirst() instanceof NumberFormatException, "Got back a NumberFormatException");
    }

    @Test
    public void testChainingOfFailure() {
        ArrayList<Exception> errors = new ArrayList<>();
        ExceptionalResponse<Integer> response = buildString("1", true, errors::add)
            .then(Integer::parseInt, errors::add);
        assertTrue(response.wasError(), "There was an error");
        assertEquals(1, errors.size(), "Got back one exception");
        assertTrue(errors.getFirst() instanceof IllegalStateException, "Got back a IllegalStateException");
    }

    public ExceptionalResponse<String> buildString(final String s, final boolean throwException, final ExceptionalListener onError) {
        return ExceptionalSupplier.of(() -> {
                if (throwException) {
                    throw new IllegalStateException("This test will fail");
                }
                return s;
            })
            .with(onError)
            .execute();
    }

    public ExceptionalResponse<Integer> convertString(final ExceptionalListener onError, final String s) {    
        ExceptionalResponse<Integer> ret = ExceptionalFunction.of(this::fromString)
            .with(onError)
            .execute(s);
        return ret;
    }

    Integer fromString(final String s) throws NumberFormatException {
        return Integer.valueOf(Integer.parseInt(s));
    }
    
}
