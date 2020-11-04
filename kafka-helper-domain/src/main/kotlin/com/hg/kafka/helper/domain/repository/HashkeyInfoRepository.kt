package com.hg.kafka.helper.domain.repository

import com.hg.kafka.helper.domain.entity.HashkeyInfo
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface HashkeyInfoRepository : MongoRepository<HashkeyInfo, String> {
    fun findAllByTopic(topic: String): List<HashkeyInfo>
    fun findOneByTopicAndHashkey(topic: String, hashkey: String): Optional<HashkeyInfo>
    fun findOneByGroupId(groupId: String): Optional<HashkeyInfo>
}