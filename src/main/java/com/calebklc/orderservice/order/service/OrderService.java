package com.calebklc.orderservice.order.service;

import com.calebklc.orderservice.order.api.request.PlaceOrderRequest;
import com.calebklc.orderservice.order.entity.Order;

public interface OrderService {
    Order placeOrder(PlaceOrderRequest request);
}
