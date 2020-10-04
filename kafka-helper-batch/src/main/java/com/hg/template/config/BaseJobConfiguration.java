package com.hg.template.config;

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import(BatchConfig.class)
public class BaseJobConfiguration {
    @Autowired
    protected JobBuilderFactory jobBuilder;
    @Autowired
    protected StepBuilderFactory stepBuilder;
}
