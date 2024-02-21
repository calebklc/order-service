package com.calebklc.orderservice.core.constant;

public enum BizError implements BaseError {
    /**
     * System related error
     */
    SYSTEM_UNKNOWN_ERROR("System unknown error"),

    /**
     * Order related error
     */
    ORDER_PLACED_FAILED("Order placed failed"),
    ORDER_NOT_FOUND("Order not found"),
    ORDER_ALREADY_TAKEN("Order already taken"),
    ACCEPT_TAKEN_ONLY("Only TAKEN status can be accepted"),

    /**
     * Distance matrix related error
     */
    DISTANCE_MATRIX_FAILED("Unable to get distance matrix"),

    /**
     * Google Maps API related error
     */
    GOOGLE_MAPS_API_FAILED("Google Maps API failed");


    private final String message;

    BizError(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
