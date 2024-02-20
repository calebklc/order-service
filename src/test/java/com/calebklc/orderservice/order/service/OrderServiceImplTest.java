package com.calebklc.orderservice.order.service;


import com.calebklc.orderservice.TestConstant;
import com.calebklc.orderservice.core.constant.BizError;
import com.calebklc.orderservice.core.exception.BizException;
import com.calebklc.orderservice.core.util.UUIDUtil;
import com.calebklc.orderservice.external.service.DistanceMatrixService;
import com.calebklc.orderservice.order.api.request.PlaceOrderRequest;
import com.calebklc.orderservice.order.constant.OrderStatus;
import com.calebklc.orderservice.order.entity.Order;
import com.calebklc.orderservice.order.mapper.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private DistanceMatrixService distanceMatrixService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private String mockOrigins;
    private String mockDestinations;
    private String mockUUID;
    private PlaceOrderRequest request;

    @BeforeEach
    void setUp() {
        mockOrigins = String.join(",", TestConstant.VALID_ORIGIN);
        mockDestinations = String.join(",", TestConstant.VALID_DESTINATION);
        String[] mockOrigin = TestConstant.VALID_ORIGIN;
        String[] mockDestination = TestConstant.VALID_DESTINATION;
        mockUUID = TestConstant.BIZ_ID;
        request = PlaceOrderRequest.builder()
                .origin(mockOrigin)
                .destination(mockDestination)
                .build();
    }

    @Test
    @DisplayName("When place order then return order succeeds")
    void whenPlaceOrderThenReturnOrderSucceeds() {
        long expectedDistance = TestConstant.VALID_DISTANCE;
        int expectedId = 1;

        try (MockedStatic<UUIDUtil> uuidUtil = Mockito.mockStatic(UUIDUtil.class)) {
            uuidUtil.when(UUIDUtil::v4).thenReturn(mockUUID);
            when(distanceMatrixService.getDistance(mockOrigins, mockDestinations)).thenReturn(expectedDistance);
            when(orderMapper.insert(Order.builder()
                                            .bizId(mockUUID)
                                            .distance(expectedDistance)
                                            .status(OrderStatus.UNASSIGNED.name())
                                            .build())).thenReturn(expectedId);

            Order order = orderService.placeOrder(request);

            assertEquals(expectedDistance, order.getDistance());
            assertEquals(OrderStatus.UNASSIGNED.name(), order.getStatus());
            assertEquals(mockUUID, order.getBizId());
        }
    }

    @Test
    @DisplayName("When place order with get distance exception then return order fails")
    void whenPlaceOrderWithGetDistanceExceptionThenReturnOrderFails() {
        when(distanceMatrixService.getDistance(mockOrigins, mockDestinations)).thenThrow(new BizException(BizError.DISTANCE_MATRIX_FAILED));
        try {
            orderService.placeOrder(request);
        } catch (Exception e) {
            assertEquals(BizError.ORDER_PLACED_FAILED.getMessage(), e.getMessage());
        }
    }

    @Test
    @DisplayName("When place order with database error then return order fails")
    void whenPlaceOrderWithDatabaseErrorThenReturnOrderFails() {
        long expectedDistance = TestConstant.VALID_DISTANCE;
        int expectedId = -1;

        try (MockedStatic<UUIDUtil> uuidUtil = Mockito.mockStatic(UUIDUtil.class)) {
            uuidUtil.when(UUIDUtil::v4).thenReturn(mockUUID);
            when(distanceMatrixService.getDistance(mockOrigins, mockDestinations)).thenReturn(expectedDistance);
            when(orderMapper.insert(Order.builder()
                                            .bizId(mockUUID)
                                            .distance(expectedDistance)
                                            .status(OrderStatus.UNASSIGNED.name())
                                            .build())).thenReturn(expectedId);

            try {
                orderService.placeOrder(request);
            } catch (Exception e) {
                assertEquals(BizError.ORDER_PLACED_FAILED.getMessage(), e.getMessage());
            }
        }
    }
}

