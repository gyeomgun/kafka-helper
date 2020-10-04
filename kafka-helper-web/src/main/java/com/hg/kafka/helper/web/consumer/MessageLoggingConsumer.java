package com.hg.kafka.helper.web.consumer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.hg.kafka.helper.domain.entity.MessageLog;
import com.hg.kafka.helper.domain.repository.MessageLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Slf4j
public class MessageLoggingConsumer {
    @Autowired
    private MessageLogRepository messageLogRepository;

    private ObjectMapper mapper = new ObjectMapper();

    public MessageLoggingConsumer() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.addHandler(new DeserializationProblemHandler() {
            @Override
            public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser jp,
                                                 JsonDeserializer<?> deserializer, Object beanOrClass,
                                                 String propertyName)
                    throws IOException, JsonProcessingException {
                log.warn("[JSON:UNKNOWN_PROP] content: {}, unknown property: {}", beanOrClass, propertyName);
                return super.handleUnknownProperty(ctxt, jp, deserializer, beanOrClass, propertyName);
            }
        });
    }

    @KafkaListener(topics = "#{topics}", containerFactory = "messageLoggingContainerFactory")
    public void receive(ConsumerRecord<String, String> consumerRecord) {
        try {
            Map map = mapper.readValue(consumerRecord.value(), Map.class);
            MessageLog messageLog = new MessageLog();
            messageLog.setTopic(consumerRecord.topic());
            messageLog.setMessageId(map.get("messageId").toString());
            messageLog.setMessageJson(consumerRecord.value());
            messageLog.setCreatedAt(new Date());

            messageLogRepository.save(messageLog);
        } catch (JsonProcessingException e) {
            log.error("Error during message logging");
        }

    }
}
