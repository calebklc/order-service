package com.calebklc.orderservice.order.api.request;

import com.calebklc.orderservice.order.validator.ValidGeoCoordinates;
import lombok.Builder;


@Builder
public record PlaceOrderRequest(@ValidGeoCoordinates String[] origin, @ValidGeoCoordinates String[] destination) {
}
