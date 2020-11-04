package com.hg.kafka.helper.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document("message_log")
data class MessageLog(
        @Id var id: String? = null,
        @Field("msg_id") var messageId: String,
        @Field("msg") var messageJson: String,
        @Field("topic") var topic: String,
        @Field("env") var environment: String,
        @Field("created_at") var createdAt: Date = Date()
)