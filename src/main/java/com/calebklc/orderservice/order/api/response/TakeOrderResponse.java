package com.calebklc.orderservice.order.api.response;


import com.calebklc.orderservice.order.constant.CommonConstant;
import lombok.Data;

@Data
public class TakeOrderResponse {
    private final String status = CommonConstant.SUCCESS;
}
