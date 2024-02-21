package com.calebklc.orderservice.order.mapper;


import com.calebklc.orderservice.TestConstant;
import com.calebklc.orderservice.core.util.PaginationUtil;
import com.calebklc.orderservice.order.constant.OrderStatus;
import com.calebklc.orderservice.order.entity.Order;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("integration")
public class OrderMapperTest {
    @Autowired
    private OrderMapper orderMapper;

    @AfterEach
    void tearDown() {
        orderMapper.deleteByBizId(TestConstant.BIZ_ID);
    }

    @Test
    void insert() {
        Order order = createOrder();
        int id = orderMapper.insert(order);

        assertThat(id).isGreaterThan(0);
    }

    @Test
    void findByBizId() {
        Order order = createOrder();
        orderMapper.insert(order);

        Order found = orderMapper.findByBizId(TestConstant.BIZ_ID).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getBizId()).isEqualTo(TestConstant.BIZ_ID);
    }

    @Test
    void updateStatus() {
        Order order = createOrder();
        orderMapper.insert(order);

        Order found = orderMapper.findByBizId(TestConstant.BIZ_ID).get();
        String newStatus = OrderStatus.TAKEN.name();
        found.setStatus(newStatus);

        int affectRows = orderMapper.updateStatus(found);
        assertThat(affectRows).isEqualTo(1);

        Order updated = orderMapper.findByBizId(TestConstant.BIZ_ID).orElse(null);
        assertThat(updated).isNotNull();
        assertThat(updated.getStatus()).isEqualTo(newStatus);
        assertThat(updated.getVersion()).isEqualTo(found.getVersion() + 1);
    }

    @Test
    @DisplayName("Race condition for changing status")
    void raceConditionForChangingStatus() {
        Order order = createOrder();
        orderMapper.insert(order);

        String newStatus = OrderStatus.TAKEN.name();

        Order order1 = orderMapper.findByBizId(TestConstant.BIZ_ID).get();
        order1.setStatus(newStatus);

        Order order2 = orderMapper.findByBizId(TestConstant.BIZ_ID).get();
        order2.setStatus(newStatus);

        int order2AffectRows = orderMapper.updateStatus(order2);
        int order1AffectRows = orderMapper.updateStatus(order1);

        assertThat(order1AffectRows).isEqualTo(0);
        assertThat(order2AffectRows).isEqualTo(1);

        Order updated = orderMapper.findByBizId(TestConstant.BIZ_ID).orElse(null);
        assertThat(updated).isNotNull();
        assertThat(updated.getStatus()).isEqualTo(newStatus);
        assertThat(updated.getVersion()).isEqualTo(order.getVersion() + 1);
    }

    @Test
    void deleteByBizId() {
        Order order = createOrder();
        orderMapper.insert(order);

        int affectRows = orderMapper.deleteByBizId(TestConstant.BIZ_ID);
        assertThat(affectRows).isEqualTo(1);

        Order deleted = orderMapper.findByBizId(TestConstant.BIZ_ID).orElse(null);
        assertThat(deleted).isNull();
    }

    @Test
    void selectByPage() {
        Order order = createOrder();
        orderMapper.insert(order);

        int offset = PaginationUtil.calculateOffset(TestConstant.PAGE, TestConstant.LIMIT);

        assertThat(orderMapper.findByPagination(TestConstant.LIMIT, offset)).isNotEmpty();
    }

    @Test
    void deleteAll() {
        Order order = createOrder();
        orderMapper.insert(order);

        orderMapper.deleteAll(true);

        assertThat(orderMapper.findByPagination(100, 0)).isEmpty();
    }

    private Order createOrder() {
        return Order.builder()
                .bizId(TestConstant.BIZ_ID)
                .distance(TestConstant.VALID_DISTANCE)
                .status(OrderStatus.UNASSIGNED.name())
                .version(0)
                .build();
    }
}
