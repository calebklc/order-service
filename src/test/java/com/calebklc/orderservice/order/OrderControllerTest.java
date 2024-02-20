package com.calebklc.orderservice.order;

import com.calebklc.orderservice.TestConstant;
import com.calebklc.orderservice.order.api.request.PlaceOrderRequest;
import com.calebklc.orderservice.order.constant.OrderStatus;
import com.calebklc.orderservice.order.entity.Order;
import com.calebklc.orderservice.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                .andExpect(status().isCreated())
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
}