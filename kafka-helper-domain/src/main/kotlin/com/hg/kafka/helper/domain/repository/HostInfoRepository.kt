package com.hg.kafka.helper.domain.repository

import com.hg.kafka.helper.domain.entity.HostInfo
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface HostInfoRepository : MongoRepository<HostInfo, String> {
    fun findAllByHashkey(hashkey: String?): List<HostInfo>
    fun findOneByHostName(hostName: String?): Optional<HostInfo>
    fun findAllByUpdatedAtLessThan(UpdatedAt: Date): List<HostInfo>
}
