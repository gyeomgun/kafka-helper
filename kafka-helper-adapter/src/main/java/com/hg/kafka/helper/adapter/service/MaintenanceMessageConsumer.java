package com.hg.kafka.helper.adapter.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.hg.kafka.helper.adapter.dto.MaintenanceDTO;
import com.hg.kafka.helper.adapter.service.maintenance.AbstractMaintenanceProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import java.io.IOException;
import java.util.List;

@Slf4j
public class MaintenanceMessageConsumer {
    @Autowired
    private List<AbstractMaintenanceProcessor> maintenanceProcessors;
    private ObjectMapper mapper;

    public MaintenanceMessageConsumer() {
        mapper = new ObjectMapper();
        mapper.addHandler(new DeserializationProblemHandler() {
            @Override
            public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser jp,
                                                 JsonDeserializer<?> deserializer, Object beanOrClass,
                                                 String propertyName)
                    throws IOException {
                return true;
            }
        });

    }

    @KafkaListener(topics = "${kafka.topic.maintenance}", containerFactory = "maintenanceContainerFactory")
    public void receive(String payload) {
        try {
            MaintenanceDTO result = mapper.readValue(payload, MaintenanceDTO.class);
            for (AbstractMaintenanceProcessor maintenanceProcessor : maintenanceProcessors) {
                if (maintenanceProcessor.isContain(result.getMaintenanceType())) {
                    maintenanceProcessor.process(result);
                }
            }
        } catch (InvalidFormatException ex) {
            log.debug("Could not convert String to enum", ex);
        } catch (IOException e) {
            log.error("Exception occupied during Maintenance message consuming" + e.getMessage(), e);
        }
    }
}