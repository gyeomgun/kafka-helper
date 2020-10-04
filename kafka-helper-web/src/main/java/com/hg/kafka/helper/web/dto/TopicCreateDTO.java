package com.hg.kafka.helper.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopicCreateDTO {
    private String name;
    private Integer partitionCount;
    private Short replicationCount;
}
