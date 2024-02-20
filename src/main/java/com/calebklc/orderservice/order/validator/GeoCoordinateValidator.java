package com.calebklc.orderservice.order.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class GeoCoordinateValidator implements ConstraintValidator<ValidGeoCoordinates, String[]> {
    @Override
    public boolean isValid(String[] coordinates, ConstraintValidatorContext context) {
        if (coordinates == null || coordinates.length < 2 || coordinates.length > 3) {
            return false;
        }

        try {
            double latitude = Double.parseDouble(coordinates[0]);
            double longitude = Double.parseDouble(coordinates[1]);

            if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }
}