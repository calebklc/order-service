package com.calebklc.orderservice;

import com.calebklc.orderservice.order.api.request.PlaceOrderRequest;
import com.calebklc.orderservice.order.api.response.PlaceOrderResponse;
import com.calebklc.orderservice.order.constant.OrderStatus;
import com.calebklc.orderservice.order.entity.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "36000")
@ActiveProfiles("integration")
class OrderServiceApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void whenPlaceOrderThenShouldReturnOrder() {
        PlaceOrderRequest placeOrderRequest = PlaceOrderRequest.builder()
                .origin(TestConstant.VALID_ORIGIN)
                .destination(TestConstant.VALID_DESTINATION)
                .build();

        Order expectedOrder = Order.builder()
                .distance(TestConstant.VALID_DISTANCE)
                .status(OrderStatus.UNASSIGNED.name())
                .build();

        webTestClient.post()
                .uri("/orders")
                .bodyValue(placeOrderRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PlaceOrderResponse.class).value(actualOrder -> {
                    assertThat(actualOrder).isNotNull();
                    assertThat(actualOrder.distance()).isEqualTo(expectedOrder.getDistance());
                    assertThat(actualOrder.status()).isEqualTo(OrderStatus.valueOf(expectedOrder.getStatus()));
                });
    }

}
