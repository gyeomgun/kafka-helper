package com.hg.kafka.helper.example.consumer;

import com.google.common.base.Preconditions;
import com.hg.kafka.helper.adapter.config.EnableKafkaHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableKafkaHelper
public class ConsumerApplication implements ApplicationListener<ApplicationReadyEvent> {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    @Bean
    public SampleConsumer sampleConsumer() {
        return new SampleConsumer("4DE6331FB66A4FF59F27");
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Preconditions.checkState(sampleConsumer().start());
    }
}
