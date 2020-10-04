package com.hg.kafka.helper.web.controller;

import com.hg.kafka.helper.adapter.dto.CommonResponse;
import com.hg.kafka.helper.domain.entity.HostInfo;
import com.hg.kafka.helper.domain.repository.HostInfoRepository;
import com.hg.kafka.helper.web.dto.HostListDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/host")
@Slf4j
@AllArgsConstructor
public class HostInfoController {
    private final HostInfoRepository hostInfoRepository;

    @GetMapping
    public CommonResponse<List<HostListDTO>> getHostList(@RequestParam("hashkey") String hashkey) {
        List<HostInfo> hostInfos = hostInfoRepository.findAllByHashkey(hashkey);
        List<HostListDTO> hostListDTOS = hostInfos.stream()
                .map(s -> HostListDTO.builder()
                        .hostName(s.getHostName())
                        .status(s.getStatus())
                        .updatedAt(s.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        CommonResponse<List<HostListDTO>> response = new CommonResponse<>();
        response.setReturnCode("0000");
        response.setInfo(hostListDTOS);
        return response;
    }

}
