package com.calebklc.orderservice.order.api;

import com.calebklc.orderservice.order.api.request.PlaceOrderRequest;
import com.calebklc.orderservice.order.api.request.TakeOrderRequest;
import com.calebklc.orderservice.order.api.response.PlaceOrderResponse;
import com.calebklc.orderservice.order.api.response.TakeOrderResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface OrderResource {
    @PostMapping
    ResponseEntity<PlaceOrderResponse> placeOrder(@Valid @RequestBody PlaceOrderRequest request);

    @PatchMapping("/{id}")
    ResponseEntity<TakeOrderResponse> takeOrder(@PathVariable("id") String bizId, @Valid @RequestBody TakeOrderRequest request);
}
