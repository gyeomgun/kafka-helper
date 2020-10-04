package com.hg.kafka.helper.web.config;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.hg.kafka.helper.adapter.config.ConsumerConfigFactory;
import com.hg.kafka.helper.adapter.dto.MaintenanceDTO;
import com.hg.kafka.helper.adapter.enums.Topic;
import com.hg.kafka.helper.adapter.producer.KafkaHelperProducer;
import com.hg.kafka.helper.domain.config.MongoConfig;
import com.hg.kafka.helper.web.consumer.DeadLetterConsumer;
import com.hg.kafka.helper.web.consumer.MessageLoggingConsumer;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Import(MongoConfig.class)
@EnableScheduling
@ComponentScan(basePackages = "com.hg.kafka.helper.web.service")
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter implements ApplicationListener<ApplicationReadyEvent> {
    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "classpath:/templates/",
            "classpath:/static/"
    };

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
        registry.addResourceHandler("/templates/**").addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    public BeanNameViewResolver getBeanNameViewResolver() {
        BeanNameViewResolver beanNameViewResolver = new BeanNameViewResolver();
        beanNameViewResolver.setOrder(0);

        return beanNameViewResolver;
    }

    @Bean
    public ViewResolver getViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        return resolver;
    }

    @Bean
    public AdminClient adminClient() {
        Map<String, Object> configs = Maps.newConcurrentMap();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return AdminClient.create((new KafkaAdmin(configs)).getConfig());
    }

    @Bean
    public KafkaHelperProducer<MaintenanceDTO> maintenanceDTOKafkaHelperProducer() {
        return new KafkaHelperProducer<MaintenanceDTO>();
    }

    @Bean
    public DeadLetterConsumer deadLetterConsumer() {
        return new DeadLetterConsumer("deadletter_group");
    }

    @Bean
    public MessageLoggingConsumer messageLoggingConsumer() {
        return new MessageLoggingConsumer();
    }

    @Bean
    public String[] topics() {
        return Arrays.stream(Topic.values()).filter(s -> s.isLogginMessage())
                .map(s -> s.getTopic()).toArray(String[]::new);
    }
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Object, String>> messageLoggingContainerFactory(ConsumerConfigFactory config) {
        Map<String, Object> props = new HashMap<>(config.getProps());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "message-logging");

        ConcurrentKafkaListenerContainerFactory<Object, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(props));
        factory.setAutoStartup(true);
        factory.setConcurrency(1);
        return factory;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Preconditions.checkState(deadLetterConsumer().start(), "Could not start deadletter consumer");
    }
}
