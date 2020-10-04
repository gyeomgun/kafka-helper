package com.hg.kafka.helper.adapter.config;

import com.hg.kafka.helper.adapter.client.ConsumerGroupClient;
import com.hg.kafka.helper.adapter.consumer.AbstractKafkaHelperConsumer;
import com.hg.kafka.helper.adapter.consumer.HashkeyRepository;
import com.hg.kafka.helper.adapter.dto.DeadLetterDTO;
import com.hg.kafka.helper.adapter.producer.KafkaHelperProducer;
import com.hg.kafka.helper.adapter.service.MaintenanceMessageConsumer;
import com.hg.kafka.helper.adapter.service.maintenance.HeartBeatMaintenanceProcessor;
import com.hg.kafka.helper.adapter.service.maintenance.TurnOnOffMaintenanceProcessor;
import com.hg.kafka.helper.adapter.util.PartitionKeyDeserializer;
import feign.Contract;
import feign.Feign;
import feign.Logger;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.slf4j.Slf4jLogger;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaHelperConsumerConfig implements KafkaListenerConfigurer {

    @Autowired(required = false)
    @Lazy
    private List<AbstractKafkaHelperConsumer> consumerList;

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.concurrency:1}")
    private Integer concurrency;

    @Value("${kafka.session.timeout.ms:10000}")
    private int sessionTimeoutMs;

    @Value("${kafka.max.poll.interval.ms:300000}")
    private int maxPollIntervalMs;

    @Value("${kafka.max.poll.records:500}")
    private int maxPollRecords;

    @Value("${kafka.maintenance-consumer.auto-startup:true}")
    private Boolean useMaintenanceConsumer;

    @Value("${kafka.helper.interface.url}")
    private String interfaceUrl;


    @Bean
    public ConsumerConfigFactory consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, PartitionKeyDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeoutMs);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalMs);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);


        return ConsumerConfigFactory.builder().props(props).build();
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Object, String>> maintenanceContainerFactory() {
        Map<String, Object> props = new HashMap<>(consumerConfigs().getProps());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, AbstractKafkaHelperConsumer.getPIDWithHostname());

        ConcurrentKafkaListenerContainerFactory<Object, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(props));
        factory.setAutoStartup(useMaintenanceConsumer);
        factory.setConcurrency(concurrency);
        return factory;
    }

    @Bean
    public ConsumerFactory<Object, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs().getProps());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Object, String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Object, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setAutoStartup(false);
        factory.setConcurrency(concurrency);
        return factory;
    }

    @Override
    public void configureKafkaListeners(KafkaListenerEndpointRegistrar registrar) {
        if (CollectionUtils.isEmpty(consumerList)) {
            return;
        }

        for (AbstractKafkaHelperConsumer consumer : consumerList) {
            if (consumer == null) {
                continue;
            }
            consumer.setAfterConsumerProperty();
            registrar.registerEndpoint(consumer, kafkaListenerContainerFactory());
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public HashkeyRepository remoteHashkeyRepository() {
        ConsumerGroupClient consumerGroupClient = Feign.builder()
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .logger(new Slf4jLogger(ConsumerGroupClient.class))
                .logLevel(Logger.Level.BASIC)
                .contract(new Contract.Default())
                .target(ConsumerGroupClient.class, interfaceUrl);
        return consumerGroupClient;
    }

    @Bean
    @Qualifier("deadLetterProducer")
    public KafkaHelperProducer<DeadLetterDTO> deadLetterProducer() {
        return new KafkaHelperProducer<>();
    }

    @Bean
    public MaintenanceMessageConsumer maintenanceMessageConsumer() {
        MaintenanceMessageConsumer consumer = new MaintenanceMessageConsumer();
        return consumer;
    }

    @Bean
    public HeartBeatMaintenanceProcessor heartBeatMaintenanceProcessor() {
        return new HeartBeatMaintenanceProcessor();
    }

    @Bean
    public TurnOnOffMaintenanceProcessor turnOnOffMaintenanceProcessor() {
        return new TurnOnOffMaintenanceProcessor();
    }
}
