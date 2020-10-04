package com.hg.kafka.helper.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsumerGroupDTO {
    private String id;
    private String name;
    private String desc;
    private String groupId;
    private String hashkey;
    private Date createdAt;
}
