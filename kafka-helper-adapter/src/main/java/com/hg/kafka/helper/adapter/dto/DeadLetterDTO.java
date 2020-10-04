package com.hg.kafka.helper.adapter.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeadLetterDTO extends KafkaDefaultDTO {
    private String messageId;
    private String messageJson;
    private String reason;
    private String topic;
    private String status;
    private Integer partition;
    private Long offset;
    private String environment;
    private Date createdAt;
    private Date updatedAt;
    private String groupId;
    private String id;
    private String serviceName;
}
