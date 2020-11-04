package com.hg.kafka.helper.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document("hashkey_info")
data class HashkeyInfo(
        @Id var id: String? = null,
        @Field("hashkey") var hashkey: String,
        @Field("name") var name: String,
        @Field("topic") var topic: String,
        @Field("desc") var description: String,
        @Field("group_id") var groupId: String,
        @Field("created_at") var createdAt: Date,
        @Field("updated_at") var updatedAt: Date
)