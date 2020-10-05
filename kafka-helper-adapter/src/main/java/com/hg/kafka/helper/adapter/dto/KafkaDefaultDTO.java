package com.hg.kafka.helper.adapter.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hg.kafka.helper.adapter.util.IdGen;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class KafkaDefaultDTO {

    private String consumerGroupId = null;
    private String kafkaTraceId = IdGen.nextUuid();

    public Object getPartitionKey() {
        return null;
    }

    public abstract String getMessageId();
}
