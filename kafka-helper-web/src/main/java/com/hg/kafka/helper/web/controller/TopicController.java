package com.hg.kafka.helper.web.controller;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hg.kafka.helper.adapter.dto.CommonResponse;
import com.hg.kafka.helper.adapter.enums.Topic;
import com.hg.kafka.helper.web.dto.TopicCreateDTO;
import com.hg.kafka.helper.web.dto.TopicListDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/topic")
@AllArgsConstructor
@Slf4j
public class TopicController {
    private final AdminClient adminClient;

    @PostConstruct
    public void init() {
        Arrays.stream(Topic.values())
                .forEach(s -> {
                    try {
                        TopicCreateDTO create = new TopicCreateDTO();
                        create.setName(s.getTopic());
                        create.setPartitionCount(s.getPartitionSize());
                        create.setReplicationCount(s.getReplicationSize());
                        createTopic(create);
                    } catch (Exception ex) {
                    }
                });

    }

    @PostMapping
    public CommonResponse<Void> createTopic(@RequestBody TopicCreateDTO topicCreateDTO) throws ExecutionException, InterruptedException {
        Preconditions.checkArgument(!StringUtils.isEmpty(topicCreateDTO.getName()), "Topic name must not be empty");
        CommonResponse<Void> response = new CommonResponse<>();

        String topicName = topicCreateDTO.getName().toLowerCase().trim();

        Preconditions.checkState(
                !adminClient.listTopics().names().get().stream().filter(s -> topicName.equals(s)).findAny().isPresent()
                , "Already exists topic name"
        );

        NewTopic newTopic = new NewTopic(topicName, topicCreateDTO.getPartitionCount(), topicCreateDTO.getReplicationCount());
        CreateTopicsResult result = adminClient.createTopics(Lists.newArrayList(newTopic));

        response.setReturnCode("0000");
        return response;
    }

    @GetMapping
    public CommonResponse<List<TopicListDTO>> getTopic() throws ExecutionException, InterruptedException {
        Set<String> topicNames = adminClient.listTopics().names().get();

        DescribeTopicsResult result = adminClient.describeTopics(topicNames);
        List<TopicListDTO> topicListDTOList = result.all().get().entrySet().stream()
                .map(s -> TopicListDTO.builder()
                        .name(s.getKey())
                        .partitionCount(s.getValue().partitions().size())
                        .replicationCount((short) s.getValue().partitions().stream().mapToInt(r -> r.replicas().size()).sum())
                        .build()
                ).collect(Collectors.toList());

        CommonResponse<List<TopicListDTO>> response = new CommonResponse<>();
        response.setReturnCode("0000");
        response.setInfo(topicListDTOList);

        return response;
    }

    @DeleteMapping
    public CommonResponse<Void> deleteTopic(@RequestParam("name") String name) {
        Optional<Topic> managedTopic = Arrays.stream(Topic.values())
                .filter(s -> s.getTopic().equals(name))
                .findFirst();

        managedTopic.ifPresent(s ->
                Preconditions.checkState(!s.isDeleteProtection(), "Delete protection activated"));

        DeleteTopicsResult deleteTopicsResult = adminClient.deleteTopics(Sets.newHashSet(name));

        CommonResponse<Void> response = new CommonResponse<>();
        response.setReturnCode("0000");

        return response;
    }
}
