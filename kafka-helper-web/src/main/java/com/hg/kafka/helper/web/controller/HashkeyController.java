package com.hg.kafka.helper.web.controller;

import com.google.common.base.Preconditions;
import com.hg.kafka.helper.adapter.client.ConsumerGroupClient;
import com.hg.kafka.helper.adapter.dto.CommonResponse;
import com.hg.kafka.helper.adapter.dto.ConsumerPropertyDTO;
import com.hg.kafka.helper.domain.entity.HashkeyInfo;
import com.hg.kafka.helper.domain.entity.HostInfo;
import com.hg.kafka.helper.domain.repository.HashkeyInfoRepository;
import com.hg.kafka.helper.domain.repository.HostInfoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@AllArgsConstructor
@Slf4j
public class HashkeyController implements ConsumerGroupClient {
    private final HashkeyInfoRepository hashkeyInfoRepository;
    private final HostInfoRepository hostInfoRepository;

    @Override
    @GetMapping("/hashkey/{hashkey}")
    public CommonResponse<ConsumerPropertyDTO> getProperty(@PathVariable("hashkey") String hashkey,
                                                           @RequestParam("host") String host,
                                                           @RequestParam("topic") String topic,
                                                           @RequestParam("status") String status,
                                                           @RequestParam(name = "env", required = false) String env) {
        Optional<HashkeyInfo> optInfo = hashkeyInfoRepository.findOneByTopicAndHashkey(topic, hashkey);
        Preconditions.checkState(optInfo.isPresent(), "Could not found groupId");
        ConsumerPropertyDTO consumerProperty =
                optInfo.map(s -> ConsumerPropertyDTO.builder()
                        .hashkey(hashkey)
                        .groupId(s.getGroupId())
                        .build())
                        .get();

        updateStatus(hashkey, host, status);

        CommonResponse<ConsumerPropertyDTO> response = new CommonResponse<>();
        response.setReturnCode("0000");
        response.setInfo(consumerProperty);
        return response;
    }

    @Override
    @PutMapping("/hashkey/{hashkey}/host/{host}/status/{status}")
    public void updateStatus(@PathVariable String hashkey,
                             @PathVariable String host,
                             @PathVariable String status) {
        HostInfo hostInfo = hostInfoRepository.findById(host)
                .orElse(new HostInfo(null, host, host, hashkey, new Date()));

        hostInfo.setStatus(status);
        hostInfo.setUpdatedAt(new Date());

        hostInfoRepository.save(hostInfo);
    }
}

