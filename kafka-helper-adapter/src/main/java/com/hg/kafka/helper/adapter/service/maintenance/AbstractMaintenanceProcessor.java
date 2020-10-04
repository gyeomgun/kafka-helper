package com.hg.kafka.helper.adapter.service.maintenance;

import com.hg.kafka.helper.adapter.consumer.AbstractKafkaHelperConsumer;
import com.hg.kafka.helper.adapter.dto.MaintenanceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;

public abstract class AbstractMaintenanceProcessor {
    @Autowired(required = false)
    @Lazy
    protected List<AbstractKafkaHelperConsumer> consumerList;

    public abstract boolean isContain(MaintenanceDTO.MaintenanceType type);
    public abstract void process(MaintenanceDTO result);
}
