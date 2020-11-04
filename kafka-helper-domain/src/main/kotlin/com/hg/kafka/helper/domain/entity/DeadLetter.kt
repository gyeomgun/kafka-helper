package com.hg.kafka.helper.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document("dead_letter")
data class DeadLetter(
        @Id var id: String? = null,
        @Field("msg_id") var messageId: String,
        @Field("msg") var messageJson: String,
        @Field("reason") var reason: String,
        @Field("topic") var topic: String,
        @Field("status") var status: String,
        @Field("partition") var partition: Int,
        @Field("offset") var offset: Long,
        @Field("env") var environment: String,
        @Field("created_at") var createdAt: Date,
        @Field("updated_at") var updatedAt: Date,
        @Field("trace_id") var kafkaTraceId: String,
        @Field("group_id") var groupId: String
)
