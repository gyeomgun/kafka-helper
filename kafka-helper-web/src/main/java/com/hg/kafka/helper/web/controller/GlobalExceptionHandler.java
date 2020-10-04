package com.hg.kafka.helper.web.controller;

import com.hg.kafka.helper.adapter.dto.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<?> handleIllegalArgumentException(IllegalArgumentException e) {
        CommonResponse<?> response = new CommonResponse<>();
        response.setReturnCode("9000");
        response.setReturnMessage(e.getMessage());
        log.error("error ", e);
        return response;
    }

    @ExceptionHandler(value = {IllegalStateException.class})
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<?> handleIllegalStateException(IllegalStateException e) {
        CommonResponse<?> response = new CommonResponse<>();
        response.setReturnCode("9001");
        response.setReturnMessage(e.getMessage());
        log.error("error ", e);
        return response;
    }
}
