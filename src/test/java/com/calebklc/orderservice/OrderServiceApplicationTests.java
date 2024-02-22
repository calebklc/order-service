package com.calebklc.orderservice;

import com.calebklc.orderservice.core.api.ErrorResponse;
import com.calebklc.orderservice.core.constant.BizError;
import com.calebklc.orderservice.order.api.request.PlaceOrderRequest;
import com.calebklc.orderservice.order.api.request.TakeOrderRequest;
import com.calebklc.orderservice.order.api.response.PlaceOrderResponse;
import com.calebklc.orderservice.order.api.response.TakeOrderResponse;
import com.calebklc.orderservice.order.constant.CommonConstant;
import com.calebklc.orderservice.order.constant.OrderStatus;
import com.calebklc.orderservice.order.entity.Order;
import com.calebklc.orderservice.order.mapper.OrderMapper;
import com.calebklc.orderservice.order.vo.OrderVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "36000")
@ActiveProfiles("integration")
class OrderServiceApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private OrderMapper orderMapper;

    @AfterEach
    void tearDown() {
        orderMapper.deleteAll(true);
    }

    @Test
    @DisplayName("Place order")
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
                .expectStatus().isOk()
                .expectBody(PlaceOrderResponse.class).value(actualOrder -> {
                    assertThat(actualOrder).isNotNull();
                    assertThat(actualOrder.distance()).isEqualTo(expectedOrder.getDistance());
                    assertThat(actualOrder.status()).isEqualTo(OrderStatus.valueOf(expectedOrder.getStatus()));
                });
    }

    @Test
    @DisplayName("Place order with invalid origin")
    void whenPlaceOrderWithInvalidOriginThenShouldReturnBadRequest() {
        PlaceOrderRequest placeOrderRequest = PlaceOrderRequest.builder()
                .origin(TestConstant.INVALID_ORIGIN)
                .destination(TestConstant.VALID_DESTINATION)
                .build();

        webTestClient.post()
                .uri("/orders")
                .bodyValue(placeOrderRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class).value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.error()).isEqualTo("Invalid geographic coordinates");
                });
    }

    @Test
    @DisplayName("Place order with invalid destination")
    void whenPlaceOrderWithInvalidDestinationThenShouldReturnBadRequest() {
        PlaceOrderRequest placeOrderRequest = PlaceOrderRequest.builder()
                .origin(TestConstant.VALID_ORIGIN)
                .destination(TestConstant.INVALID_DESTINATION)
                .build();

        webTestClient.post()
                .uri("/orders")
                .bodyValue(placeOrderRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class).value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.error()).isEqualTo("Invalid geographic coordinates");
                });
    }

    @Test
    @DisplayName("Take order")
    void whenTakeOrderThenShouldSuccessfullyTakeOrder() {
        seedOrder();

        TakeOrderRequest takeOrderRequest = new TakeOrderRequest(OrderStatus.TAKEN.name());

        webTestClient.patch()
                .uri("/orders/" + TestConstant.BIZ_ID)
                .bodyValue(takeOrderRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TakeOrderResponse.class).value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getStatus()).isEqualTo(CommonConstant.SUCCESS);
                });
    }

    @Test
    @DisplayName("Take order with invalid status")
    void whenTakeOrderWithInvalidStatusThenShouldReturnBadRequest() {
        TakeOrderRequest takeOrderRequest = new TakeOrderRequest("INVALID");

        webTestClient.patch()
                .uri("/orders/" + TestConstant.BIZ_ID)
                .bodyValue(takeOrderRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class).value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.error()).isEqualTo(BizError.ACCEPT_TAKEN_ONLY.getMessage());
                });
    }

    @Test
    @DisplayName("Take order with invalid order id")
    void whenTakeOrderWithInvalidIdThenShouldReturnNotFound() {
        TakeOrderRequest takeOrderRequest = new TakeOrderRequest(OrderStatus.TAKEN.name());

        webTestClient.patch()
                .uri("/orders/INVALID")
                .bodyValue(takeOrderRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class).value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.error()).isEqualTo(BizError.ORDER_NOT_FOUND.getMessage());
                });
    }

    @Test
    @DisplayName("Take order with taken order id")
    void whenTakeOrderWithTakenOrderIdThenShouldReturnBadRequest() {
        seedOrder();

        TakeOrderRequest takeOrderRequest = new TakeOrderRequest(OrderStatus.TAKEN.name());

        webTestClient.patch()
                .uri("/orders/" + TestConstant.BIZ_ID)
                .bodyValue(takeOrderRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TakeOrderResponse.class).value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getStatus()).isEqualTo(CommonConstant.SUCCESS);
                });

        webTestClient.patch()
                .uri("/orders/" + TestConstant.BIZ_ID)
                .bodyValue(takeOrderRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class).value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.error()).isEqualTo(BizError.ORDER_ALREADY_TAKEN.getMessage());
                });
    }

    @Test
    @DisplayName("Concurrent take order")
    void whenTakeOrderConcurrentlyThenShouldOnly1OrderCanBeTaken() throws InterruptedException {
        seedOrder();

        TakeOrderRequest takeOrderRequest = new TakeOrderRequest(OrderStatus.TAKEN.name());

        int numberOfThreads = Runtime.getRuntime().availableProcessors() * 2;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CyclicBarrier barrier = new CyclicBarrier(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger();

        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(() -> {
                try {
                    // Wait for all threads to be ready
                    barrier.await();

                    // Then execute the request
                    webTestClient.patch()
                            .uri("/orders/" + TestConstant.BIZ_ID)
                            .bodyValue(takeOrderRequest)
                            .exchange()
                            .expectStatus().isOk()
                            .expectBody(TakeOrderResponse.class).value(response -> {
                                if (response != null && CommonConstant.SUCCESS.equals(response.getStatus())) {
                                    successCount.incrementAndGet();
                                }
                            });
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // Wait for all threads to finish
        service.shutdown();
        service.awaitTermination(1, TimeUnit.MINUTES);

        assertThat(successCount.get()).isEqualTo(1);
    }

    @Test
    @DisplayName("Fetch orders")
    void whenFetchOrdersThenShouldReturnOrders() {
        Order order = seedOrder();

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/orders")
                        .queryParam("page", TestConstant.PAGE)
                        .queryParam("limit", TestConstant.LIMIT)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(OrderVO.class).value(orders -> {
                    assertThat(orders).isNotNull();
                    assertThat(orders.size()).isEqualTo(1);
                    assertThat(orders.get(0).id()).isEqualTo(order.getBizId());
                    assertThat(orders.get(0).distance()).isEqualTo(order.getDistance());
                    assertThat(orders.get(0).status()).isEqualTo(OrderStatus.valueOf(order.getStatus()));
                });
    }

    @Test
    @DisplayName("Fetch orders with invalid page")
    void whenFetchOrdersWithInvalidPageThenShouldReturnEmpty() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/orders")
                        .queryParam("page", -1)
                        .queryParam("limit", TestConstant.LIMIT)
                        .build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class).value(errorResponse -> {
                    assertThat(errorResponse).isNotNull();
                    assertThat(errorResponse.error()).isEqualTo("The page must be equal or greater than 1");
                });
    }

    @Test
    @DisplayName("Fetch orders with invalid limit")
    void whenFetchOrdersWithInvalidLimitThenShouldReturnEmpty() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/orders")
                        .queryParam("page", TestConstant.PAGE)
                        .queryParam("limit", -1)
                        .build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class).value(errorResponse -> {
                    assertThat(errorResponse).isNotNull();
                    assertThat(errorResponse.error()).isEqualTo("The limit must be equal or greater than 1");
                });
    }

    @Test
    @DisplayName("Fetch orders with no orders")
    void whenFetchOrdersWithNoOrdersThenShouldReturnEmpty() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/orders")
                        .queryParam("page", TestConstant.PAGE * 5)
                        .queryParam("limit", TestConstant.LIMIT)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(OrderVO.class).value(orders -> {
                    assertThat(orders).isNotNull();
                    assertThat(orders.size()).isEqualTo(0);
                });
    }

    private Order seedOrder() {
        Order order = Order.builder()
                .bizId(TestConstant.BIZ_ID)
                .distance(TestConstant.VALID_DISTANCE)
                .status(OrderStatus.UNASSIGNED.name())
                .version(0)
                .build();

        orderMapper.insert(order);

        return order;
    }
}
