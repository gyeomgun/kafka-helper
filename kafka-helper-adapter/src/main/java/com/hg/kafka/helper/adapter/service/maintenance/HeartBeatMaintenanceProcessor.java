package com.hg.kafka.helper.adapter.service.maintenance;

import com.google.common.util.concurrent.RateLimiter;
import com.hg.kafka.helper.adapter.consumer.AbstractKafkaHelperConsumer;
import com.hg.kafka.helper.adapter.dto.MaintenanceDTO;
import org.springframework.util.CollectionUtils;

public class HeartBeatMaintenanceProcessor extends AbstractMaintenanceProcessor {
    private static RateLimiter rateLimiter = RateLimiter.create(1);

    @Override
    public boolean isContain(MaintenanceDTO.MaintenanceType type) {
        return MaintenanceDTO.MaintenanceType.HEARTBEAT.equals(type);
    }

    @Override
    public void process(MaintenanceDTO result) {
        if (CollectionUtils.isEmpty(consumerList)) {
            return;
        }

        if (!rateLimiter.tryAcquire()) {
            return;
        }

        for (AbstractKafkaHelperConsumer consumer : consumerList) {
            if (null == consumer) {
                continue;
            }
            consumer.sendHeartBeat();
        }
    }
}

