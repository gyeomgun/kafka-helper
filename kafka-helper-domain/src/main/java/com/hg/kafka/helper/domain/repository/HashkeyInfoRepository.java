package com.hg.kafka.helper.domain.repository;

import com.hg.kafka.helper.domain.entity.HashkeyInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HashkeyInfoRepository extends MongoRepository<HashkeyInfo, String> {
    List<HashkeyInfo> findAllByTopic(String topic);
    Optional<HashkeyInfo> findOneByTopicAndHashkey(String topic, String hashkey);
    Optional<HashkeyInfo> findOneByGroupId(String groupId);
}
