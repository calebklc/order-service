package com.calebklc.orderservice.core.util;

import java.util.UUID;

public class UUIDUtil {

    private UUIDUtil() {
    }

    public static String v4() {
        return UUID.randomUUID().toString();
    }
}
