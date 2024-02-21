package com.calebklc.orderservice.order.api;

import com.calebklc.orderservice.order.api.request.PlaceOrderRequest;
import com.calebklc.orderservice.order.api.request.TakeOrderRequest;
import com.calebklc.orderservice.order.api.response.PlaceOrderResponse;
import com.calebklc.orderservice.order.api.response.TakeOrderResponse;
import com.calebklc.orderservice.order.vo.OrderVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

public interface OrderResource {
    @PostMapping
    ResponseEntity<PlaceOrderResponse> placeOrder(@Valid @RequestBody PlaceOrderRequest request);

    @PatchMapping("/{id}")
    ResponseEntity<TakeOrderResponse> takeOrder(@PathVariable("id") String bizId, @Valid @RequestBody TakeOrderRequest request);

    @GetMapping
    ResponseEntity<Collection<OrderVO>> fetchOrders(@RequestParam(value = "page", defaultValue = "1", required = false)
                                                    @Min(value = 1, message = "The page must be equal or greater than 1")
                                                    @Max(value = 100, message = "The page must be equal or less than 100")
                                                    int page,
                                                    @RequestParam(value = "limit", defaultValue = "20", required = false)
                                                    @Min(value = 1, message = "The limit must be equal or greater than 1")
                                                    @Max(value = 100, message = "The limit must be equal or less than 100")
                                                    int limit);
}
