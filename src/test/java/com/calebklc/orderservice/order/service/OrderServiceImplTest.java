package com.calebklc.orderservice.order.service;


import com.calebklc.orderservice.TestConstant;
import com.calebklc.orderservice.core.constant.BizError;
import com.calebklc.orderservice.core.exception.BizException;
import com.calebklc.orderservice.core.util.PaginationUtil;
import com.calebklc.orderservice.core.util.UUIDUtil;
import com.calebklc.orderservice.external.service.DistanceMatrixService;
import com.calebklc.orderservice.order.api.request.PlaceOrderRequest;
import com.calebklc.orderservice.order.api.request.TakeOrderRequest;
import com.calebklc.orderservice.order.constant.OrderStatus;
import com.calebklc.orderservice.order.entity.Order;
import com.calebklc.orderservice.order.mapper.OrderMapper;
import com.calebklc.orderservice.order.vo.OrderVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
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

    @Test
    @DisplayName("When take order then return order succeeds")
    void whenTakeOrderThenReturnOrderSucceeds() {
        Order order = Order.builder()
                .bizId(mockUUID)
                .distance(TestConstant.VALID_DISTANCE)
                .status(OrderStatus.UNASSIGNED.name())
                .version(0)
                .build();
        when(orderMapper.findByBizId(mockUUID)).thenReturn(Optional.of(order));
        when(orderMapper.updateStatus(order)).thenReturn(1);

        assertDoesNotThrow(() -> orderService.takeOrder(mockUUID, new TakeOrderRequest(OrderStatus.TAKEN.name())));
        verify(orderMapper).updateStatus(order);
    }

    @Test
    @DisplayName("When take order with invalid status then return order fails")
    void whenTakeOrderWithInvalidStatusThenReturnOrderFails() {
        try {
            orderService.takeOrder(mockUUID, new TakeOrderRequest("INVALID"));
        } catch (Exception e) {
            assertEquals(BizError.ACCEPT_TAKEN_ONLY.getMessage(), e.getMessage());
        }
    }

    @Test
    @DisplayName("When take order with order not found then return order fails")
    void whenTakeOrderWithOrderNotFoundThenReturnOrderFails() {
        when(orderMapper.findByBizId(mockUUID)).thenReturn(Optional.empty());
        try {
            orderService.takeOrder(mockUUID, new TakeOrderRequest(OrderStatus.TAKEN.name()));
        } catch (Exception e) {
            assertEquals(BizError.ORDER_NOT_FOUND.getMessage(), e.getMessage());
        }
    }

    @Test
    @DisplayName("When take order with order already taken then return order fails")
    void whenTakeOrderWithOrderAlreadyTakenThenReturnOrderFails() {
        Order order = Order.builder()
                .bizId(mockUUID)
                .distance(TestConstant.VALID_DISTANCE)
                .status(OrderStatus.TAKEN.name())
                .version(1)
                .build();
        when(orderMapper.findByBizId(mockUUID)).thenReturn(java.util.Optional.of(order));
        try {
            orderService.takeOrder(mockUUID, new TakeOrderRequest(OrderStatus.TAKEN.name()));
        } catch (Exception e) {
            assertEquals(BizError.ORDER_ALREADY_TAKEN.getMessage(), e.getMessage());
        }
    }

    @Test
    @DisplayName("When take order with update status error then return order fails")
    void whenTakeOrderWithUpdateStatusErrorThenReturnOrderFails() {
        Order order = Order.builder()
                .bizId(mockUUID)
                .distance(TestConstant.VALID_DISTANCE)
                .status(OrderStatus.UNASSIGNED.name())
                .version(0)
                .build();
        when(orderMapper.findByBizId(mockUUID)).thenReturn(Optional.of(order));
        when(orderMapper.updateStatus(order)).thenReturn(0);
        try {
            orderService.takeOrder(mockUUID, new TakeOrderRequest(OrderStatus.TAKEN.name()));
        } catch (Exception e) {
            assertEquals(BizError.ORDER_ALREADY_TAKEN.getMessage(), e.getMessage());
        }
    }

    @Test
    void whenFetchOrdersThenReturnOrders() {
        Order order = Order.builder()
                .bizId(mockUUID)
                .distance(TestConstant.VALID_DISTANCE)
                .status(OrderStatus.UNASSIGNED.name())
                .version(0)
                .build();

        int offset = PaginationUtil.calculateOffset(TestConstant.PAGE, TestConstant.LIMIT);

        when(orderMapper.findByPagination(TestConstant.LIMIT, offset)).thenReturn(Collections.singletonList(order));

        Collection<OrderVO> fetchedOrder = orderService.fetchOrders(TestConstant.PAGE, TestConstant.LIMIT);

        assertEquals(1, fetchedOrder.size());
    }

    @Test
    void whenFetchOrdersWithInvalidPageThenReturnEmpty() {
        int page = TestConstant.PAGE * 100;
        int limit = TestConstant.LIMIT * 5;
        int offset = PaginationUtil.calculateOffset(page, limit);

        when(orderMapper.findByPagination(limit, offset)).thenReturn(Collections.emptyList());
        Collection<OrderVO> fetchedOrder = orderService.fetchOrders(page, limit);

        assertEquals(0, fetchedOrder.size());
    }
}
