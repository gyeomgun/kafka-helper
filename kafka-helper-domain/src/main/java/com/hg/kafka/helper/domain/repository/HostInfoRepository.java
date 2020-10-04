package com.hg.kafka.helper.domain.repository;

import com.hg.kafka.helper.domain.entity.HostInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface HostInfoRepository extends MongoRepository<HostInfo, String> {
    List<HostInfo> findAllByHashkey(String hashkey);
    Optional<HostInfo> findOneByHostName(String hostName);
    List<HostInfo> findAllByUpdatedAtLessThan(Date UpdatedAt);
}
