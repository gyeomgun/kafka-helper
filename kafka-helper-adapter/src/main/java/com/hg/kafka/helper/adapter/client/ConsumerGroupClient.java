package com.hg.kafka.helper.adapter.client;

import com.hg.kafka.helper.adapter.consumer.HashkeyRepository;
import com.hg.kafka.helper.adapter.dto.CommonResponse;
import com.hg.kafka.helper.adapter.dto.ConsumerPropertyDTO;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface ConsumerGroupClient extends HashkeyRepository {
    @RequestLine("GET /hashkey/{hashkey}?host={host}&topic={topic}&status={status}&env={env}")
    @Headers("Content-Type: application/json")
    CommonResponse<ConsumerPropertyDTO> getProperty(@Param("hashkey") String hashkey,
                                                    @Param("host") String host,
                                                    @Param("topic") String topic,
                                                    @Param("status") String status,
                                                    @Param("env") String env);

    @RequestLine("PUT /hashkey/{hashkey}/host/{host}/status/{status}")
    @Headers("Content-Type: application/json")
    void updateStatus(@Param("hashkey") String hashkey, @Param("host") String host,
                      @Param("status") String status);
}
