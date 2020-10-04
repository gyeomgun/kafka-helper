package com.hg.kafka.helper.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collection = "host_info")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HostInfo {
    @Id
    private String id;
    @Field("host_name")
    private String hostName;
    @Field("hash_key")
    private String hashkey;
    @Field("status")
    private String status;
    @Field("updated_at")
    private Date updatedAt;
}
