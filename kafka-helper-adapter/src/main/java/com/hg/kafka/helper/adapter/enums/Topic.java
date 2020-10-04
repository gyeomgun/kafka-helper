package com.hg.kafka.helper.adapter.enums;

import com.hg.kafka.helper.adapter.dto.DeadLetterDTO;
import com.hg.kafka.helper.adapter.dto.KafkaDefaultDTO;
import com.hg.kafka.helper.adapter.dto.MaintenanceDTO;
import com.hg.kafka.helper.adapter.dto.SampleDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Topic {
    TOPIC_DEAD_LETTER(DeadLetterDTO.class, "common-deadletter-v1", 1, (short) 1, true, false),
    TOPIC_MAINTENANCE_BROADCAST(MaintenanceDTO.class, "common-maintenance-v1", 1, (short) 1, true, false),
    TOPIC_SAMPLE(SampleDTO.class, "common-sample-v1", 1, (short) 1, false, true);
    private Class<? extends KafkaDefaultDTO> cls;
    private String topic;
    private int partitionSize;
    private short replicationSize;
    private boolean deleteProtection;
    private boolean logginMessage;
}
