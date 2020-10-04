package com.hg.kafka.helper.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collection = "hashkey_info")
@Data
public class HashkeyInfo {
    @Id
    private String id;
    @Field("hashkey")
    private String hashkey;
    @Field("name")
    private String name;
    @Field("topic")
    private String topic;
    @Field("desc")
    private String description;
    @Field("group_id")
    private String groupId;
    @Field("created_at")
    private Date createdAt;
    @Field("updated_at")
    private Date updatedAt;
}
