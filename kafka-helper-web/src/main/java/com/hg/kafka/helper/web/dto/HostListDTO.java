package com.hg.kafka.helper.web.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HostListDTO {
    private String hostName;
    private String status;
    private Date updatedAt;
}
