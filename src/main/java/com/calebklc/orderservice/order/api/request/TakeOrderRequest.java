package com.calebklc.orderservice.order.api.request;

import com.calebklc.orderservice.core.constant.BizError;
import com.calebklc.orderservice.core.exception.BizException;
import com.calebklc.orderservice.order.constant.OrderStatus;
import jakarta.validation.constraints.NotBlank;


public record TakeOrderRequest(@NotBlank String status) {
}
