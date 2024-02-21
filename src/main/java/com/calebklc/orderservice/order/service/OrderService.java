package com.calebklc.orderservice.order.service;

import com.calebklc.orderservice.order.api.request.PlaceOrderRequest;
import com.calebklc.orderservice.order.api.request.TakeOrderRequest;
import com.calebklc.orderservice.order.entity.Order;
import org.springframework.transaction.annotation.Transactional;

public interface OrderService {
    Order placeOrder(PlaceOrderRequest request);

    @Transactional
    void takeOrder(String bizId, TakeOrderRequest request);
}
