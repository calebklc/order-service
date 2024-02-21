package com.calebklc.orderservice.order;

import com.calebklc.orderservice.order.api.OrderResource;
import com.calebklc.orderservice.order.api.request.PlaceOrderRequest;
import com.calebklc.orderservice.order.api.request.TakeOrderRequest;
import com.calebklc.orderservice.order.api.response.PlaceOrderResponse;
import com.calebklc.orderservice.order.api.response.TakeOrderResponse;
import com.calebklc.orderservice.order.constant.OrderStatus;
import com.calebklc.orderservice.order.entity.Order;
import com.calebklc.orderservice.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController implements OrderResource {

    private final OrderService orderService;

    @Override
    public ResponseEntity<PlaceOrderResponse> placeOrder(PlaceOrderRequest request) {
        Order order = orderService.placeOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PlaceOrderResponse.builder()
                              .id(order.getBizId())
                              .distance(order.getDistance())
                              .status(OrderStatus.valueOf(order.getStatus()))
                              .build());
    }

    @Override
    public ResponseEntity<TakeOrderResponse> takeOrder(String bizId, TakeOrderRequest request) {
        orderService.takeOrder(bizId, request);

        return ResponseEntity.ok(new TakeOrderResponse());
    }
}
