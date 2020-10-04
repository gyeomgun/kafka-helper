package com.hg.kafka.helper.example.consumer;

import com.hg.kafka.helper.adapter.consumer.AbstractKafkaHelperConsumer;
import com.hg.kafka.helper.adapter.dto.SampleDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

@Slf4j
public class SampleConsumer extends AbstractKafkaHelperConsumer<SampleDTO> {
    public SampleConsumer(String hashkey) {
        super(hashkey, true);
    }

    @Override
    public void process(SampleDTO result) throws Exception {
        log.info(result.getMessageId());
        throw new IllegalArgumentException("test");
    }

    @Override
    public void onFailure(SampleDTO object, Exception exception, ConsumerRecord<String, String> record) {
        log.error("Error", exception);
    }
}
