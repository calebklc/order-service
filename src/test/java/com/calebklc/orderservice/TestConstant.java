package com.calebklc.orderservice;

public class TestConstant {
    public static final String[] VALID_ORIGIN = new String[]{"22.367712", "114.060111"};
    public static final String[] VALID_DESTINATION = new String[]{"22.244717", "114.158532"};
    public static final long VALID_DISTANCE = 29108;

    public static final String BIZ_ID = "123e4567-e89b-12d3-a456-426614174000";

    public static final String[] INVALID_ORIGIN = new String[]{"invalid", "114.060111"};
    public static final String[] INVALID_DESTINATION = new String[]{"22.244717", "coordinates"};
}
