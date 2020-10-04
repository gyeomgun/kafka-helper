package com.hg.kafka.helper.web.service;

import com.hg.kafka.helper.adapter.dto.MaintenanceDTO;
import com.hg.kafka.helper.adapter.producer.KafkaHelperProducer;
import com.hg.kafka.helper.domain.entity.HostInfo;
import com.hg.kafka.helper.domain.repository.HostInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class HeartbeatService {
    static final long ONE_MINUTE_IN_MILLIS = 60000; //millisecs

    @Autowired
    private KafkaHelperProducer<MaintenanceDTO> maintenanceDTOKafkaHelperProducer;
    @Autowired
    private HostInfoRepository hostInfoRepository;

    @Scheduled(fixedDelay = 1_000)
    public void publishHeartbeat() {
        MaintenanceDTO heartbeatMsg = MaintenanceDTO.builder()
                .maintenanceType(MaintenanceDTO.MaintenanceType.HEARTBEAT)
                .build();
        try {
            maintenanceDTOKafkaHelperProducer.send(heartbeatMsg);
        } catch (Exception e) {
            log.error("Exception during publish heartbeat msg", e);
        }
    }

    @Scheduled(fixedDelay = 10_000)
    public void deleteOldHostInfo() {
        Calendar date = Calendar.getInstance();
        long t = date.getTimeInMillis();
        Date removeDateBefore = new Date(t - (5 * ONE_MINUTE_IN_MILLIS));

        List<HostInfo> hostInfos = hostInfoRepository.findAllByUpdatedAtLessThan(removeDateBefore);

        hostInfoRepository.deleteAll(hostInfos);
    }
}
