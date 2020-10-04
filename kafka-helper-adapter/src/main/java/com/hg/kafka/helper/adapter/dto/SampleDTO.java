package com.hg.kafka.helper.adapter.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SampleDTO extends KafkaDefaultDTO {
    private Integer seq;
    private String payload1;
    private String payload2;
    private String payload3;
    @Override
    public String getMessageId() {
        return seq.toString();
    }
}
