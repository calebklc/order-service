package com.calebklc.orderservice.core.exception;

import com.calebklc.orderservice.core.api.ErrorResponse;
import com.calebklc.orderservice.core.constant.BizError;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(Exception e) {
        log.error("Exception caught", e);

        return new ResponseEntity<>(new ErrorResponse(BizError.SYSTEM_UNKNOWN_ERROR.getMessage()),
                                    HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BizException.class)
    public ResponseEntity<ErrorResponse> handle(BizException e) {
        log.error("BizException caught", e);

        return new ResponseEntity<>(new ErrorResponse(e.getMessage()),
                                    HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException e) {
        log.debug("MethodArgumentNotValidException caught", e);

        FieldError fieldError = e.getBindingResult().getFieldError();
        assert fieldError != null;

        log.debug("Field: {}, FieldError: {}", fieldError.getField(), fieldError.getDefaultMessage());

        return new ResponseEntity<>(new ErrorResponse(fieldError.getDefaultMessage()),
                                    HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handle(ConstraintViolationException e) {
        log.error("ConstraintViolationException caught", e);

        String errorMessage = new ArrayList<>(e.getConstraintViolations()).getFirst().getMessage();

        return new ResponseEntity<>(new ErrorResponse(errorMessage),
                                    HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handle(MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException caught", e);

        return new ResponseEntity<>(new ErrorResponse(BizError.INVALID_PARAMETER.getMessage()),
                                    HttpStatus.BAD_REQUEST);
    }
}
