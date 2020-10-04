package com.hg.kafka.helper.adapter.config;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class ConsumerConfigFactory {
    Map<String, Object> props;
}
