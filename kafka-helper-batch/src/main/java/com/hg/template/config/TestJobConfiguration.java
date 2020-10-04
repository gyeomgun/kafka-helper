package com.hg.template.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class TestJobConfiguration extends BaseJobConfiguration {
    @Bean(name = "testJob")
    public Job testJob() {
        return jobBuilder.get("testJob")
                .start(stepBuilder.get("testStep")
                        .tasklet(new Tasklet() {
                            @Override
                            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                                log.info("Test success");
                                return RepeatStatus.FINISHED;
                            }
                        }).build())
                .build();
    }
}
