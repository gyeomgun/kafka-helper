package com.hg.kafka.helper.web.consumer;

import com.hg.kafka.helper.adapter.consumer.AbstractKafkaHelperConsumer;
import com.hg.kafka.helper.adapter.dto.DeadLetterDTO;
import com.hg.kafka.helper.domain.entity.DeadLetter;
import com.hg.kafka.helper.domain.repository.DeadLetterRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Slf4j
public class DeadLetterConsumer extends AbstractKafkaHelperConsumer<DeadLetterDTO> {
    @Autowired
    private DeadLetterRepository deadLetterRepository;

    public DeadLetterConsumer(String hashkey) {
        super(hashkey);
    }

    @Override
    public void process(DeadLetterDTO result) throws Exception {
        DeadLetter deadLetter = new DeadLetter();
        deadLetter.setTopic(result.getTopic());
        deadLetter.setStatus("PENDING");
        deadLetter.setReason(result.getReason());
        deadLetter.setPartition(result.getPartition());
        deadLetter.setOffset(result.getOffset());
        deadLetter.setMessageJson(result.getMessageJson());
        deadLetter.setMessageId(result.getMessageId());
        deadLetter.setEnvironment(result.getEnvironment());
        deadLetter.setKafkaTraceId(result.getKafkaTraceId());
        deadLetter.setGroupId(result.getGroupId());
        deadLetter.setCreatedAt(new Date());
        deadLetter.setUpdatedAt(new Date());
        deadLetterRepository.save(deadLetter);
    }

    @Override
    public void onFailure(DeadLetterDTO object, Exception exception, ConsumerRecord<String, String> record) {
        log.error("Error during store deadletter", exception);
    }
}
