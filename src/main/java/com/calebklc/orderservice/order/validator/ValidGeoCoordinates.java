package com.calebklc.orderservice.order.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = GeoCoordinateValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGeoCoordinates {
    String message() default "Invalid geographic coordinates";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}