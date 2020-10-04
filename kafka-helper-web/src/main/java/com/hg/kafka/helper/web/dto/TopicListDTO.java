package com.hg.kafka.helper.web.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopicListDTO {
    private String name;
    private Integer partitionCount;
    private Short replicationCount;
}
