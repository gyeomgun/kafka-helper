package com.hg.kafka.helper.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document("dead_letter")
@Data
public class DeadLetter {
    @Id
    private String id;
    @Field("msg_id")
    private String messageId;
    @Field("msg")
    private String messageJson;
    @Field("reason")
    private String reason;
    @Field("topic")
    private String topic;
    @Field("status")
    private String status;
    @Field("partition")
    private Integer partition;
    @Field("offset")
    private Long offset;
    @Field("env")
    private String environment;
    @Field("created_at")
    private Date createdAt;
    @Field("updated_at")
    private Date updatedAt;
    @Field("trace_id")
    private String kafkaTraceId;
    @Field("group_id")
    private String groupId;

}