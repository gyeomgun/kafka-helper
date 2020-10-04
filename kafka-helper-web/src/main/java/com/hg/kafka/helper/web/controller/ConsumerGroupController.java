package com.hg.kafka.helper.web.controller;

import com.google.common.base.Preconditions;
import com.hg.kafka.helper.adapter.dto.CommonResponse;
import com.hg.kafka.helper.domain.entity.HashkeyInfo;
import com.hg.kafka.helper.domain.repository.HashkeyInfoRepository;
import com.hg.kafka.helper.web.dto.ConsumerGroupDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/consumer")
@AllArgsConstructor
@Slf4j
public class ConsumerGroupController {
    private final HashkeyInfoRepository hashkeyInfoRepository;

    @GetMapping("/hashkey")
    public CommonResponse<String> generateHashkey() {
        String hashkey = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "").substring(0, 20);
        CommonResponse<String> response = new CommonResponse<>();
        response.setReturnCode("0000");
        response.setInfo(hashkey);
        return response;
    }

    @PostMapping("/{topic}/group")
    public CommonResponse<Void> registrationConsumerGroup(@PathVariable String topic,
                                                          @RequestBody ConsumerGroupDTO group) {
        Preconditions.checkArgument(!StringUtils.isEmpty(group.getHashkey()), "`hashkey` must not be empty");
        Preconditions.checkArgument(!StringUtils.isEmpty(group.getGroupId()), "`groupId` must not be empty");
        Preconditions.checkArgument(!StringUtils.isEmpty(group.getName()), "`name` must not be empty");

        HashkeyInfo hashkeyInfo = new HashkeyInfo();
        hashkeyInfo.setUpdatedAt(new Date());
        hashkeyInfo.setCreatedAt(new Date());
        hashkeyInfo.setTopic(topic);
        hashkeyInfo.setName(group.getName());
        hashkeyInfo.setGroupId(group.getGroupId());
        hashkeyInfo.setDescription(group.getDesc());
        hashkeyInfo.setHashkey(group.getHashkey());

        hashkeyInfoRepository.save(hashkeyInfo);

        CommonResponse<Void> response = new CommonResponse<>();
        response.setReturnCode("0000");
        return response;
    }

    @GetMapping("/{topic}/group")
    public CommonResponse<List<ConsumerGroupDTO>> getConsumerGroup(@PathVariable String topic) {
        List<HashkeyInfo> hashkeyInfos = hashkeyInfoRepository.findAllByTopic(topic);

        List<ConsumerGroupDTO> groups = hashkeyInfos.stream()
                .map(s -> ConsumerGroupDTO.builder()
                        .id(s.getId())
                        .hashkey(s.getHashkey())
                        .name(s.getName())
                        .groupId(s.getGroupId())
                        .desc(s.getDescription())
                        .createdAt(s.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        CommonResponse<List<ConsumerGroupDTO>> response = new CommonResponse<>();
        response.setReturnCode("0000");
        response.setInfo(groups);
        return response;
    }

    @DeleteMapping("/group/{id}")
    public CommonResponse<Void> deleteConsumerGroup(@PathVariable String id) {

        hashkeyInfoRepository.deleteById(id);

        CommonResponse<Void> response = new CommonResponse<>();
        response.setReturnCode("0000");
        return response;
    }


//    @PutMapping("/hashkey/{hashkey}/host/{host}/status/{status}")
//    public String updateStatus(@PathVariable String hashkey,
//                               @PathVariable String host,
//                               @PathVariable String status) {
//        return "ok";
//    }
//
//    @GetMapping("/hashkey/{hashkey}")
//    public CommonResponse<ConsumerPropertyDTO> getProperty(@PathVariable String hashkey,
//                                                           @RequestParam("host") String pidWIthHostName,
//                                                           @RequestParam String topic,
//                                                           @RequestParam(value = "status", required = false) String status) {
//        return null;
//    }
}