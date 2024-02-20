package com.calebklc.orderservice.order.api.response;

import com.calebklc.orderservice.order.constant.OrderStatus;
import lombok.Builder;

@Builder
public record PlaceOrderResponse(String id, Long distance, OrderStatus status) {
}
