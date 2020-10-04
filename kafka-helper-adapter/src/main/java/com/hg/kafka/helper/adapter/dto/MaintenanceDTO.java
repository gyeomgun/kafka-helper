package com.hg.kafka.helper.adapter.dto;

import lombok.*;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MaintenanceDTO extends KafkaDefaultDTO {
    private MaintenanceType maintenanceType;
    private String topic;
    private String hashkey;
    private String hostName;
    private Map<String, Object> parameters;

    @Override
    public String getMessageId() {
        return UUID.randomUUID().toString();
    }

    public enum MaintenanceType {
        HEARTBEAT
        , CONSUMER_ON
        , CONSUMER_OFF
    }
}
