package com.hg.kafka.helper.web.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageLogDTO {
    public String id;
    private String messageId;
    private String messageJson;
    private String topic;
    private String environment;
    private Date createdAt;
}
