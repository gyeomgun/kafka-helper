package com.hg.kafka.helper.adapter.consumer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.hg.kafka.helper.adapter.dto.CommonResponse;
import com.hg.kafka.helper.adapter.dto.ConsumerPropertyDTO;
import com.hg.kafka.helper.adapter.dto.DeadLetterDTO;
import com.hg.kafka.helper.adapter.dto.KafkaDefaultDTO;
import com.hg.kafka.helper.adapter.enums.Topic;
import com.hg.kafka.helper.adapter.exceptions.DeserializeException;
import com.hg.kafka.helper.adapter.producer.KafkaHelperProducer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.config.AbstractKafkaListenerEndpoint;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.listener.adapter.HandlerAdapter;
import org.springframework.kafka.listener.adapter.MessagingMessageListenerAdapter;
import org.springframework.kafka.listener.adapter.RecordMessagingMessageListenerAdapter;
import org.springframework.kafka.support.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;
import java.util.*;

@Slf4j
public abstract class AbstractKafkaHelperConsumer<T extends KafkaDefaultDTO>
        extends AbstractKafkaListenerEndpoint<String, String> implements ConsumerSeekAware {

    private static final String LINE_FEED = "\n";
    private static final String OFFSET_FROM_BEGINNING = "earliest";
    private static final String OFFSET_FROM_LATEST = "latest";

    private final ObjectMapper mapper = new ObjectMapper();

    @Getter
    private String hashkey;
    private boolean publishDeadLetter;
    private MessageListenerContainer container;
    private Method method;

    @Autowired(required = false)
    @Lazy
    @Qualifier("deadLetterProducer")
    private KafkaHelperProducer<DeadLetterDTO> deadLetterProducer;

    @Autowired
    @Lazy
    private HashkeyRepository repository;

    @Getter
    @Value("${kafka.profile.active:local}")
    private String kafkaProfile;
    @Value("${kafka.offset.from:current}")
    private String fromOffset;

    private AbstractKafkaHelperConsumer() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.addHandler(new DeserializationProblemHandler() {
            @Override
            public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser jp,
                                                 JsonDeserializer<?> deserializer, Object beanOrClass,
                                                 String propertyName)
                    throws IOException, JsonProcessingException {
                return super.handleUnknownProperty(ctxt, jp, deserializer, beanOrClass, propertyName);
            }
        });
    }

    public AbstractKafkaHelperConsumer(String hashkey) {
        this(hashkey, false);
    }

    public AbstractKafkaHelperConsumer(String hashkey, boolean publishDeadLetter) {
        this();
        this.hashkey = hashkey;
        this.publishDeadLetter = publishDeadLetter;
    }

    public static String getPIDWithHostname() {
        return ManagementFactory.getRuntimeMXBean().getName();
    }

    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.warn("Could not found hostName ", e);
        }
        return "HostName" + UUID.randomUUID();
    }

    public abstract void process(T result) throws Exception;

    public abstract void onFailure(T object, Exception exception, ConsumerRecord<String, String> record);

    @Override
    protected MessagingMessageListenerAdapter<String, String> createMessageListener(MessageListenerContainer container, MessageConverter messageConverter) {
        RecordMessagingMessageListenerAdapter<String, String> listenerAdapter =
                new RecordMessagingMessageListenerAdapter<>(this, method);
        listenerAdapter.setHandlerMethod(new HandlerAdapter(new InvocableHandlerMethod(this, method)));

        this.container = container;

        return listenerAdapter;
    }

    @Override
    public void onPartitionsAssigned(Map<TopicPartition, Long> map, ConsumerSeekCallback consumerSeekCallback) {
        if (fromOffset.equals(OFFSET_FROM_LATEST)) {
            for (Map.Entry<TopicPartition, Long> elem : map.entrySet()) {
                consumerSeekCallback.seekToEnd(elem.getKey().topic(), elem.getKey().partition());
            }
        } else if (fromOffset.equals(OFFSET_FROM_BEGINNING)) {
            for (Map.Entry<TopicPartition, Long> elem : map.entrySet()) {
                consumerSeekCallback.seekToBeginning(elem.getKey().topic(), elem.getKey().partition());
            }
        }
    }

    @Override
    public void afterPropertiesSet() {
        try {

            setId(getHostName() + UUID.randomUUID());
            setTopics(getTopicArray(getCustomTopics()));
            method = AbstractKafkaHelperConsumer.class.getDeclaredMethod("receive", ConsumerRecord.class);
            super.afterPropertiesSet();
        } catch (Exception e) {
            log.error("afterPropertiesSet has exception. " + e.getMessage(), e);
            throw new ExceptionInInitializerError(e.getMessage());
        }
    }

    private void receive(ConsumerRecord<String, String> record) {

        T message = null;

        try {
            message = deserialize(record.value());

            if (StringUtils.isEmpty(message.getConsumerGroupId())) {
                message.setConsumerGroupId(getGroupId());
            }

            if (getGroupId().equals(message.getConsumerGroupId())) {
                process(message);
                String logKey = Joiner.on(":").join(message.getKafkaTraceId(), message.getMessageId());
                log.info(logKey);
            }
        } catch (Exception e) {
            log.warn(record.value(), e);
            publishDeadLetter(buildDeadLetter(message, record, e));
            onFailure(message, e, record);
        }
    }

    private T deserialize(String string) throws DeserializeException {

        T result;

        try {
            @SuppressWarnings(value = "unchecked")
            Class<T> targetClass = (Class<T>) ((ParameterizedType) getClass()
                    .getGenericSuperclass()).getActualTypeArguments()[0];

            result = mapper.readValue(string, targetClass);
        } catch (Exception e) {
            throw new DeserializeException(e);
        }

        return result;
    }

    private DeadLetterDTO buildDeadLetter(T message, ConsumerRecord<String, String> record, Exception ex) {
        if (!publishDeadLetter) {
            return null;
        }

        if (message == null) {
            return null;
        }

        Date now = new Date();

        try {
            return DeadLetterDTO.builder()
                    .messageId(message.getMessageId())
                    .environment(kafkaProfile)
                    .messageJson(record.value())
                    .topic(record.topic())
                    .offset(record.offset())
                    .partition(record.partition())
                    .reason(exceptionMessageBuilder(ex))
                    .groupId(message.getConsumerGroupId())
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
        } catch (Exception e) {
            log.warn("### Failed to build deadletterDTO", e);
            return null;
        }
    }

    private String exceptionMessageBuilder(Exception exception) {
        StringBuilder sb = new StringBuilder();

        if (null == exception) {
            return "";
        }

        sb.append("+").append(exception.getClass().getName());
        sb.append(LINE_FEED);

        for (StackTraceElement s : exception.getStackTrace()) {
            String msg = s.getClassName() + "." + Joiner.on(":").join(s.getMethodName(), s.getLineNumber());
            sb.append("-").append(msg).append(LINE_FEED);
        }

        return sb.toString();
    }

    private void publishDeadLetter(DeadLetterDTO deadLetterDTO) {
        if (!publishDeadLetter) {
            return;
        }

        if (deadLetterDTO == null) {
            return;
        }

        if (deadLetterProducer == null) {
            return;
        }

        try {
            deadLetterProducer.send(deadLetterDTO);
        } catch (Exception e) {
            log.warn("Failed to send deadLetter. " +
                    "The kafkaTraceId of deadLetter : " + deadLetterDTO.getKafkaTraceId());
        }
    }

    public void setAfterConsumerProperty() {
        String topic = getTopics().iterator().next();
        String hostName = getPIDWithHostname();

        if (!isAllowEmptyHashkey(kafkaProfile)) {
            Preconditions.checkArgument(StringUtils.hasText(hashkey),
                    "hashkey must not be null.");
        }

        if (StringUtils.isEmpty(hashkey)) {
            String groupId = getHostName();
            log.info("hashkey is empty. groupId using " + groupId);
            setGroupId(groupId);
            return;
        }

        if (topic.equals(Topic.TOPIC_DEAD_LETTER.getTopic())) {
            setGroupId(hashkey);
            return;
        }

        log.info("Registration by " + Joiner.on(",").join(hashkey, hostName, topic));

        try {
            CommonResponse<ConsumerPropertyDTO> response =
                    repository.getProperty(hashkey, getPIDWithHostname(), topic, convertStatus(), kafkaProfile);

            ConsumerPropertyDTO consumerProperty = response.getInfo();
            setGroupId(consumerProperty.getGroupId());
        } catch (Exception ex) {
            String errorMessage =
                    "Exception occurred during set consumer property";
            log.error(errorMessage, ex);
            throw new IllegalStateException(errorMessage, ex);
        }
    }

    private String convertStatus() {
        if (null == container) {
            return "SUSPEND";
        }
        return container.isRunning() ? "ALIVE" : "SUSPEND";
    }

    private boolean isAllowEmptyHashkey(String kafkaProfile) {
        return "local".equals(kafkaProfile);
    }

    private Topic[] getCustomTopics() {

        @SuppressWarnings(value = "unchecked")
        Class<T> targetClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];

        for (Topic type : Topic.values()) {
            if (type.getCls().equals(targetClass)) {
                return new Topic[] { type };
            }
        }

        return null;
    }

    private String[] getTopicArray(Topic[] customTopics) throws InvalidParameterException {
        if (customTopics == null) {
            throw new InvalidParameterException("couldn't find matched topic");
        }
        List<String> topics = new ArrayList<>(customTopics.length);

        for (Topic type : customTopics) {

            StringBuilder sb = new StringBuilder();
            sb.append(type.getTopic());

            topics.add(sb.toString());
        }

        return topics.toArray(new String[topics.size()]);
    }

    public void sendHeartBeat() {
        try {
            if (StringUtils.isEmpty(hashkey)) {
                return;
            }
            repository.updateStatus(hashkey, getPIDWithHostname(), convertStatus());
        } catch (Exception ex) {
            log.warn("Could not send heartbeat", ex);
        }
    }

    public boolean start() {
        if (container == null) {
            return false;
        }

        try {
            if (!container.isRunning()) {
                container.start();
            }
        } catch (KafkaException e) {
            return false;
        }

        return true;
    }

    public boolean stop() {
        if (container == null) {
            return false;
        }

        try {
            if (container.isRunning()) {
                container.stop();
            }
        } catch (KafkaException e) {
            return false;
        }

        return true;
    }
}
