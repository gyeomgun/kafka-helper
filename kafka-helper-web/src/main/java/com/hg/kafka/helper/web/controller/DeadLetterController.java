package com.hg.kafka.helper.web.controller;

import com.hg.kafka.helper.adapter.dto.CommonResponse;
import com.hg.kafka.helper.adapter.dto.DeadLetterDTO;
import com.hg.kafka.helper.domain.entity.DeadLetter;
import com.hg.kafka.helper.domain.repository.HashkeyInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/deadletter")
public class DeadLetterController {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private HashkeyInfoRepository hashkeyInfoRepository;

    @GetMapping
    public CommonResponse<List<DeadLetterDTO>> getDeadLetter(@RequestParam("page") Integer page,
                                                             @RequestParam(name = "size", defaultValue = "10") Integer size) {

        Query query = new Query();
        query.with(PageRequest.of(page - 1, size));
        query.with(Sort.by(Sort.Direction.DESC, "_id"));

        List<DeadLetter> deadLetters = mongoTemplate.find(query, DeadLetter.class);

        List<DeadLetterDTO> dto = deadLetters.stream()
                .map(s -> {
                    AtomicReference<String> serviceName = new AtomicReference<>("");
                    hashkeyInfoRepository.findOneByGroupId(s.getGroupId())
                            .ifPresent(h -> serviceName.set(h.getName()));
                    return DeadLetterDTO.builder()
                        .groupId(s.getGroupId())
                        .createdAt(s.getCreatedAt())
                        .environment(s.getEnvironment())
                        .updatedAt(s.getUpdatedAt())
                        .messageId(s.getMessageId())
                        .messageJson(s.getMessageJson())
                        .offset(s.getOffset())
                        .partition(s.getPartition())
                        .reason(s.getReason())
                        .topic(s.getTopic())
                        .status(s.getStatus())
                        .id(s.getId())
                        .serviceName(serviceName.get())
                        .build();
                })
                .collect(Collectors.toList());


        CommonResponse<List<DeadLetterDTO>> response = new CommonResponse<>();
        response.setInfo(dto);
        response.setReturnCode("0000");

        return response;
    }
}
