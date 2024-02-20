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

    /**
     * Distance matrix related error
     */
    DISTANCE_MATRIX_FAILED("Unable to get distance matrix"),
    DISTANCE_MATRIX_NOT_FOUND("Distance matrix not found"),
    DISTANCE_MATRIX_ZERO_RESULTS("No distance found"),

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
