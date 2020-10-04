package com.hg.kafka.helper.web.controller;

import com.hg.kafka.helper.adapter.dto.CommonResponse;
import com.hg.kafka.helper.domain.entity.MessageLog;
import com.hg.kafka.helper.web.dto.MessageLogDTO;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/message")
public class LogMessageController {
    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping
    public CommonResponse<List<MessageLogDTO>> getMessage(@RequestParam("page") Integer page,
                                                    @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Query query = new Query();
        query.with(PageRequest.of(page - 1, size));
        query.with(Sort.by(Sort.Direction.DESC, "_id"));

        List<MessageLog> deadLetters = mongoTemplate.find(query, MessageLog.class);

        List<MessageLogDTO> dto = deadLetters.stream()
                .map(s -> MessageLogDTO.builder()
                        .id(s.getId())
                        .messageId(s.getMessageId())
                        .messageJson(s.getMessageJson())
                        .topic(s.getTopic())
                        .environment(s.getEnvironment())
                        .createdAt(s.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        CommonResponse<List<MessageLogDTO>> response = new CommonResponse<>();
        response.setInfo(dto);
        response.setReturnCode("0000");

        return response;
    }
}
