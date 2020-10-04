package com.hg.kafka.helper.example.publisher;

import com.hg.kafka.helper.adapter.dto.SampleDTO;
import com.hg.kafka.helper.adapter.producer.KafkaHelperProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class TestController {
    @Autowired
    private KafkaHelperProducer<SampleDTO> samplePublisher;

    @GetMapping("pub")
    public void pub() throws Exception {
        SampleDTO dto = new SampleDTO();
        dto.setSeq(1);
        dto.setPayload1(UUID.randomUUID().toString());
        samplePublisher.send(dto);
    }
}
