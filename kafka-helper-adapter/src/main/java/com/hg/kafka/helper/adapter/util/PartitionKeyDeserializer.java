package com.hg.kafka.helper.adapter.util;

import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class PartitionKeyDeserializer implements Deserializer<Object> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public Object deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        return KryoUtils.deserialize(data);
    }

    @Override
    public void close() {}

}
