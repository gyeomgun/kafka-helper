package com.hg.kafka.helper.web;

import com.hg.kafka.helper.adapter.config.EnableKafkaHelper;
import com.hg.kafka.helper.web.config.WebConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({WebConfig.class})
@EnableKafkaHelper
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
