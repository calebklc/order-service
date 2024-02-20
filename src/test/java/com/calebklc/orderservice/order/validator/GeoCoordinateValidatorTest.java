package com.calebklc.orderservice.order.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GeoCoordinateValidatorTest {

    private final GeoCoordinateValidator validator = new GeoCoordinateValidator();

    @Test
    @DisplayName("When valid coordinates then validation succeeds")
    void whenValidCoordinatesThenValidationSucceeds() {
        assertTrue(validator.isValid(new String[]{"40.712776", "-74.005974"}, null));
    }

    @Test
    @DisplayName("When null coordinates then validation fails")
    void whenNullCoordinatesThenValidationFails() {
        assertFalse(validator.isValid(null, null));
    }

    @Test
    @DisplayName("When less than two coordinates then validation fails")
    void whenLessThanTwoCoordinatesThenValidationFails() {
        assertFalse(validator.isValid(new String[]{"40.712776"}, null));
    }

    @Test
    @DisplayName("When more than three coordinates then validation fails")
    void whenMoreThanThreeCoordinatesThenValidationFails() {
        assertFalse(validator.isValid(new String[]{"40.712776", "-74.005974", "100", "200"}, null));
    }

    @Test
    @DisplayName("When non-numeric coordinates then validation fails")
    void whenNonNumericCoordinatesThenValidationFails() {
        assertFalse(validator.isValid(new String[]{"40.712776", "invalid"}, null));
    }

    @Test
    @DisplayName("When out of range coordinates then validation fails")
    public void whenOutOfRangeCoordinatesThenValidationFails() {
        assertFalse(validator.isValid(new String[]{"1000", "-74.005974"}, null));
        assertFalse(validator.isValid(new String[]{"40.712776", "-2000"}, null));
    }
}