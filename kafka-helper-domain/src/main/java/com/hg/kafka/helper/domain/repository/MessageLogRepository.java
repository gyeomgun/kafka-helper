package com.hg.kafka.helper.domain.repository;

import com.hg.kafka.helper.domain.entity.MessageLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageLogRepository extends MongoRepository<MessageLog, String> {
}
