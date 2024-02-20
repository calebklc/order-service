package com.calebklc.orderservice.core.exception;

import com.calebklc.orderservice.core.constant.BizError;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BizException extends RuntimeException {
    private final String message;

    public BizException(String message) {
        super(message);
        this.message = message;
    }

    public BizException(BizError error) {
        super(error.getMessage());
        this.message = error.getMessage();
    }
}
