package com.hg.kafka.helper.example.publisher;

import com.hg.kafka.helper.adapter.config.EnableKafkaHelper;
import com.hg.kafka.helper.adapter.dto.SampleDTO;
import com.hg.kafka.helper.adapter.producer.KafkaHelperProducer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableKafkaHelper
public class PubApplication {
    public static void main(String[] args) {
        SpringApplication.run(PubApplication.class, args);
    }

    @Bean
    public KafkaHelperProducer<SampleDTO> sampleProducer() {
        return new KafkaHelperProducer<>();
    }
}
