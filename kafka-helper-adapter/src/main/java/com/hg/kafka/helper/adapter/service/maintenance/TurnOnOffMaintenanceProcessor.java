package com.hg.kafka.helper.adapter.service.maintenance;

import com.hg.kafka.helper.adapter.consumer.AbstractKafkaHelperConsumer;
import com.hg.kafka.helper.adapter.dto.MaintenanceDTO;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class TurnOnOffMaintenanceProcessor extends AbstractMaintenanceProcessor {
    @Override
    public boolean isContain(MaintenanceDTO.MaintenanceType type) {
        return MaintenanceDTO.MaintenanceType.CONSUMER_ON.equals(type) || MaintenanceDTO.MaintenanceType.CONSUMER_OFF.equals(type);
    }

    @Override
    public void process(MaintenanceDTO result) {
        if (CollectionUtils.isEmpty(consumerList)) {
            return;
        }

        for (AbstractKafkaHelperConsumer consumer : consumerList) {
            if (null == consumer) {
                continue;
            }

            if (!StringUtils.isEmpty(result.getTopic())) {
                if (!result.getTopic().equals(consumer.getTopics().iterator().next())) {
                    continue;
                }
            }

            if (!StringUtils.isEmpty(result.getHashkey())) {
                if (!result.getHashkey().equals(consumer.getHashkey())) {
                    continue;
                }
            }

            if (!StringUtils.isEmpty(result.getHostName())) {
                if (!result.getHostName().equals(consumer.getPIDWithHostname())) {
                    continue;
                }
            }

            if (MaintenanceDTO.MaintenanceType.CONSUMER_ON.equals(result.getMaintenanceType())) {
                consumer.start();
            }

            if (MaintenanceDTO.MaintenanceType.CONSUMER_OFF.equals(result.getMaintenanceType())) {
                consumer.stop();
            }

            consumer.sendHeartBeat();
        }
    }
}
