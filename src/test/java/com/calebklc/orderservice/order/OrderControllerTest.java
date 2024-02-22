package com.calebklc.orderservice.order;

import com.calebklc.orderservice.TestConstant;
import com.calebklc.orderservice.core.constant.BizError;
import com.calebklc.orderservice.core.exception.BizException;
import com.calebklc.orderservice.order.api.request.PlaceOrderRequest;
import com.calebklc.orderservice.order.api.request.TakeOrderRequest;
import com.calebklc.orderservice.order.constant.CommonConstant;
import com.calebklc.orderservice.order.constant.OrderStatus;
import com.calebklc.orderservice.order.entity.Order;
import com.calebklc.orderservice.order.service.OrderService;
import com.calebklc.orderservice.order.vo.OrderVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private OrderService orderService;

    @Test
    @DisplayName("When place order then should return order")
    public void whenPlaceOrderThenShouldReturnOrder() throws Exception {
        PlaceOrderRequest request = PlaceOrderRequest.builder()
                .origin(TestConstant.VALID_ORIGIN)
                .destination(TestConstant.VALID_DESTINATION)
                .build();

        Order order = Order.builder()
                .bizId(TestConstant.BIZ_ID)
                .distance(TestConstant.VALID_DISTANCE)
                .status(OrderStatus.UNASSIGNED.name())
                .build();

        when(orderService.placeOrder(any(PlaceOrderRequest.class))).thenReturn(order);

        mockMvc.perform(post("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(order.getBizId())))
                .andExpect(jsonPath("$.distance", is(order.getDistance().intValue())))
                .andExpect(jsonPath("$.status", is(order.getStatus())));
    }

    @Test
    @DisplayName("When place order with invalid coordinates then should return bad request")
    public void whenPlaceOrderWithInvalidCoordinatesThenShouldReturnBadRequest() throws Exception {
        PlaceOrderRequest request = PlaceOrderRequest.builder()
                .origin(TestConstant.INVALID_ORIGIN)
                .destination(TestConstant.INVALID_DESTINATION)
                .build();

        mockMvc.perform(post("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("When take order then should successfully take order")
    public void whenTakeOrderThenShouldReturnOrder() throws Exception {
        TakeOrderRequest request = new TakeOrderRequest(OrderStatus.TAKEN.name());

        mockMvc.perform(patch("/orders/" + TestConstant.BIZ_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(CommonConstant.SUCCESS)));
    }

    @Test
    @DisplayName("When take order with invalid status then should return bad request")
    public void whenTakeOrderWithInvalidStatusThenShouldReturnBadRequest() throws Exception {
        TakeOrderRequest request = new TakeOrderRequest("INVALID");

        doThrow(new BizException(BizError.ACCEPT_TAKEN_ONLY))
                .when(orderService).takeOrder(TestConstant.BIZ_ID, request);

        mockMvc.perform(patch("/orders/" + TestConstant.BIZ_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(BizError.ACCEPT_TAKEN_ONLY.getMessage())));
    }

    @Test
    @DisplayName("When take order with invalid biz id then should return not found")
    public void whenTakeOrderWithInvalidBizIdThenShouldReturnNotFound() throws Exception {
        TakeOrderRequest request = new TakeOrderRequest(OrderStatus.TAKEN.name());

        doThrow(new BizException(BizError.ORDER_NOT_FOUND))
                .when(orderService).takeOrder(TestConstant.BIZ_ID, request);

        mockMvc.perform(patch("/orders/" + TestConstant.BIZ_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(BizError.ORDER_NOT_FOUND.getMessage())));
    }

    @Test
    @DisplayName("When take order with already taken order then should return bad request")
    public void whenTakeOrderWithAlreadyTakenOrderThenShouldReturnBadRequest() throws Exception {
        TakeOrderRequest request = new TakeOrderRequest(OrderStatus.TAKEN.name());

        doThrow(new BizException(BizError.ORDER_ALREADY_TAKEN))
                .when(orderService).takeOrder(TestConstant.BIZ_ID, request);

        mockMvc.perform(patch("/orders/" + TestConstant.BIZ_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(BizError.ORDER_ALREADY_TAKEN.getMessage())));
    }

    @Test
    void whenFetchOrdersThenShouldReturnOrders() throws Exception {
        OrderVO orderVO = OrderVO.from(TestConstant.BIZ_ID, TestConstant.VALID_DISTANCE, OrderStatus.UNASSIGNED);

        when(orderService.fetchOrders(TestConstant.PAGE, TestConstant.LIMIT)).thenReturn(Collections.singletonList(orderVO));

        mockMvc.perform(get("/orders")
                                .queryParam("page", TestConstant.PAGE + "")
                                .queryParam("limit", TestConstant.LIMIT + "")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(orderVO.id())))
                .andExpect(jsonPath("$[0].distance", is((int) orderVO.distance())))
                .andExpect(jsonPath("$[0].status", is(orderVO.status().name())));
    }

    @Test
    void whenFetchOrdersWithInvalidPageThenShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/orders")
                                .queryParam("page", "-1")
                                .queryParam("limit", TestConstant.LIMIT + "")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("The page must be equal or greater than 1")));
    }

    @Test
    void whenFetchOrdersWithInvalidLimitThenShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/orders")
                                .queryParam("page", TestConstant.PAGE + "")
                                .queryParam("limit", "-1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("The limit must be equal or greater than 1")));
    }

    @Test
    void whenFetchOrdersWithNoResultThenShouldReturnEmpty() throws Exception {
        when(orderService.fetchOrders(TestConstant.PAGE, TestConstant.LIMIT)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/orders")
                                .queryParam("page", TestConstant.PAGE + "")
                                .queryParam("limit", TestConstant.LIMIT + "")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }
}