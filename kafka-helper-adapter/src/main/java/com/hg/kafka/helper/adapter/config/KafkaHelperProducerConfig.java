package com.hg.kafka.helper.adapter.config;

import com.hg.kafka.helper.adapter.util.MessageSerializer;
import com.hg.kafka.helper.adapter.util.PartitionKeySerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaHelperProducerConfig {

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Value("${kafka.buffer.memory:33554432}")
    private long bufferMemory;

    @Value("${kafka.retries:0}")
    private int retries;

    @Value("${kafka.retry.backoff.ms:0}")
    private int retryBackoffMs;

    @Value("${kafka.batch.size:16384}")
    private int batchSize;

    @Value("${kafka.linger.ms:50}")
    private long lingerMs;

    @Value("${kafka.request.timeout.ms:30000}")
    private int requestTimeoutMs;

    @Value("${kafka.max.block.ms:60000}")
    private long maxBlockMs;
    @Value("${kafka.acks:all}")
    private String acks;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.RETRIES_CONFIG, retries);
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, retryBackoffMs);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, PartitionKeySerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MessageSerializer.class);
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs);
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, maxBlockMs);
        props.put(ProducerConfig.ACKS_CONFIG, acks);

        return props;
    }

    @Bean
    public ProducerFactory<Object, ?> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<Object, ?> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
