package com.calebklc.orderservice.order.service;

import com.calebklc.orderservice.core.constant.BizError;
import com.calebklc.orderservice.core.exception.BizException;
import com.calebklc.orderservice.core.util.UUIDUtil;
import com.calebklc.orderservice.external.service.DistanceMatrixService;
import com.calebklc.orderservice.order.api.request.PlaceOrderRequest;
import com.calebklc.orderservice.order.constant.OrderStatus;
import com.calebklc.orderservice.order.entity.Order;
import com.calebklc.orderservice.order.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final DistanceMatrixService distanceMatrixService;
    private final OrderMapper orderMapper;

    @Override
    public Order placeOrder(PlaceOrderRequest request) {
        String origins = String.join(",", request.origin());
        String destinations = String.join(",", request.destination());

        long distance;
        try {
            distance = distanceMatrixService.getDistance(origins, destinations);
        } catch (Exception e) {
            log.error("Failed to get distance matrix", e);
            throw new BizException(BizError.ORDER_PLACED_FAILED);
        }

        Order order = Order.builder()
                .bizId(UUIDUtil.v4())
                .distance(distance)
                .status(OrderStatus.UNASSIGNED.name())
                .build();

        int id = orderMapper.insert(order);

        if (id < 0) {
            throw new BizException(BizError.ORDER_PLACED_FAILED);
        }

        return order;
    }
}
