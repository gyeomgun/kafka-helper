package com.hg.kafka.helper.adapter.producer;

import com.google.common.base.Preconditions;
import com.hg.kafka.helper.adapter.dto.KafkaDefaultDTO;
import com.hg.kafka.helper.adapter.enums.Topic;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
public class KafkaHelperProducer<T extends KafkaDefaultDTO> {

    @Value("${kafka.profile.active:local}")
    private String kafkaProfile;

    @Autowired
    private KafkaTemplate<Object, T> kafkaTemplate;

    @Setter
    private KafkaProduceSuccessListener kafkaProduceSuccessListener;
    @Setter
    private KafkaProduceFailureListener kafkaProduceFailureListener;

    private void handleSuccess(final SendResult<Object, T> payload) {
        if (kafkaProduceSuccessListener != null) {
            kafkaProduceSuccessListener.onSuccess(payload.getRecordMetadata(),
                    payload.getProducerRecord());
        }
    }

    private void handleFailure(final Throwable throwable) {
        if (throwable instanceof KafkaProducerException) {
            ProducerRecord<?, ?> record = ((KafkaProducerException) throwable).getProducerRecord();
            log.warn("onFailure\npartition : " + record.partition()
                    + "\ntopic : " + record.topic()
                    + "\nvalue : " + record.value());
        }

        if (kafkaProduceFailureListener != null) {
            kafkaProduceFailureListener.onFailure(throwable);
        }
    }

    private void handleFailure(Throwable throwable, String topic, Object payload) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, payload);
        handleFailure(new KafkaProducerException(record, throwable.getMessage(), throwable));
    }

    private ListenableFutureCallback<SendResult<Object, T>> getCallback() {
        return new ListenableFutureCallback<SendResult<Object, T>>() {
            @Override
            public void onSuccess(final SendResult<Object, T> payload) {
                handleSuccess(payload);
            }

            @Override
            public void onFailure(final Throwable throwable) {
                handleFailure(throwable);
            }
        };
    }

    public void send(final T obj) throws Exception {
        Object partitionKey = null;
        try {
            partitionKey = obj.getPartitionKey();
        } catch (Exception ex) {
            log.warn(" ### Failed to getPartitionKey ", ex);
        }
        send(obj, partitionKey);
    }

    public void send(final T obj, final Object partitionKey) throws Exception {
        String dtoTargetTopic = getTopic(obj.getClass());
        kafkaTemplate.send(dtoTargetTopic, partitionKey, obj).addCallback(getCallback());
    }

    private String getTopic(Class dtoType) {
        String targetTopic = null;

        for (Topic type : Topic.values()) {
            if (type.getCls().equals(dtoType)) {
                StringBuilder sb = new StringBuilder();
                sb.append(type.getTopic());
                targetTopic = sb.toString();
                break;
            }
        }

        Preconditions.checkArgument(!StringUtils.isEmpty(targetTopic),
                "could not found matched topic. dto " + dtoType.getName());
        return targetTopic;
    }
}
