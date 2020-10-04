package com.hg.kafka.helper.adapter.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConsumerPropertyDTO {
    private String hashkey;
    private String groupId;
}

