package com.hg.kafka.helper.adapter.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public interface KafkaProduceSuccessListener {
    void onSuccess(RecordMetadata recordMetadata, ProducerRecord producerRecord);
}
