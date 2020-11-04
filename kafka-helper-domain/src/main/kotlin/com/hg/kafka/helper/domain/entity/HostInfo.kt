package com.hg.kafka.helper.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document("host_info")
data class HostInfo(
        @Id var id: String? = null,
        @Field("host_name") var hostName: String,
        @Field("hash_key") var hashkey: String,
        @Field("status") var status: String,
        @Field("updated_at") var updatedAt: Date
)