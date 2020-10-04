package com.hg.kafka.helper.adapter.producer;

public interface KafkaProduceFailureListener {
    void onFailure(Throwable throwable);
}
