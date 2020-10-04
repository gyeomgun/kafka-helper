package com.hg.kafka.helper.adapter.util;

import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class PartitionKeySerializer implements Serializer<Object> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public byte[] serialize(String topic, Object data) {
        if (data == null) {
            return null;
        }
        return KryoUtils.serialize(data);
    }

    @Override
    public void close() {}

}
