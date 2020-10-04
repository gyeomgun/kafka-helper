package com.hg.kafka.helper.adapter.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CommonResponse<T> {
    private String returnCode;
    private String returnMessage;
    private Map<String, String> errorDetailMap;
    private T info;
}
