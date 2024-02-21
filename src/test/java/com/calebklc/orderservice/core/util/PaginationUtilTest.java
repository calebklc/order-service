package com.calebklc.orderservice.core.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PaginationUtilTest {

    @Test
    void calculateOffset() {
        int page = 1;
        int size = 10;
        int offset = PaginationUtil.calculateOffset(page, size);

        assertThat(offset).isEqualTo(0);
    }

}
