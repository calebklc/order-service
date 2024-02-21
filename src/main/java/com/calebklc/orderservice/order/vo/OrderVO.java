package com.calebklc.orderservice.order.vo;

import com.calebklc.orderservice.order.constant.OrderStatus;

public record OrderVO(String id, long distance, OrderStatus status) {
    public static OrderVO from(String id, long distance, OrderStatus status) {
        return new OrderVO(id, distance, status);
    }
}
