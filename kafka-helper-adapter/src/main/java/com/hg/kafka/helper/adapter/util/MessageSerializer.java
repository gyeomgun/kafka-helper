package com.hg.kafka.helper.adapter.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class MessageSerializer implements Serializer {

    @Override
    public void configure(Map map, boolean b) {}

    @Override
    public byte[] serialize(String key, Object val) {

        byte[] result = null;

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            result = objectMapper.writeValueAsString(val).getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void close() {}
}
