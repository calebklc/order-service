package com.calebklc.orderservice.core.util;

public class PaginationUtil {

    private PaginationUtil() {
    }

    public static int calculateOffset(int page, int limit) {
        return (page - 1) * limit;
    }
}
