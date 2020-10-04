package com.hg.kafka.helper.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document("message_log")
@Data
public class MessageLog {
    @Id
    public String id;
    @Field("msg_id")
    private String messageId;
    @Field("msg")
    private String messageJson;
    @Field("topic")
    private String topic;
    @Field("env")
    private String environment;
    @Field("created_at")
    private Date createdAt;
}
