package com.calebklc.orderservice.core.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UUIDUtilTest {

    @Test
    @DisplayName("Generate UUID v4")
    public void generateUUIDv4() {
        String uuid = UUIDUtil.v4();

        assertNotNull(uuid);
        assertTrue(uuid.matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}"));
    }
}