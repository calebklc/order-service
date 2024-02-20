package com.calebklc.orderservice.order.mapper;


import com.calebklc.orderservice.TestConstant;
import com.calebklc.orderservice.order.constant.OrderStatus;
import com.calebklc.orderservice.order.entity.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("integration")
public class OrderMapperTest {
    @Autowired
    private OrderMapper orderMapper;
    private Order order;

    @BeforeEach
    void setUp() {
        order = Order.builder()
                .bizId(TestConstant.BIZ_ID)
                .distance(TestConstant.VALID_DISTANCE)
                .status(OrderStatus.UNASSIGNED.name())
                .build();
    }

    @Test
    void insert() {
        int id = orderMapper.insert(order);

        assertThat(id).isGreaterThan(0);
        assertThat(id).isEqualTo(order.getId().intValue());
    }
}
