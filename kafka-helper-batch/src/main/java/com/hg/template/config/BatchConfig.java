package com.hg.template.config;

import com.hg.template.config.launcher.CustomJobLauncher;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.MapJobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig implements BatchConfigurer {
    @Override
    public JobRepository getJobRepository() throws Exception {
        return mapJobExplorerFactoryBean().getObject();
    }

    @Override
    public PlatformTransactionManager getTransactionManager() throws Exception {
        return new ResourcelessTransactionManager();
    }

    @Override
    public JobLauncher getJobLauncher() throws Exception {
        CustomJobLauncher jobLauncher = new CustomJobLauncher();
        jobLauncher.setJobRepository(getJobRepository());
        jobLauncher.setTaskExecutor(new SyncTaskExecutor());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Override
    public JobExplorer getJobExplorer() throws Exception {
        MapJobExplorerFactoryBean factoryBean = new MapJobExplorerFactoryBean();
        factoryBean.setRepositoryFactory(mapJobExplorerFactoryBean());
        return factoryBean.getObject();
    }

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }
    @Bean
    public MapJobRepositoryFactoryBean mapJobExplorerFactoryBean() throws Exception {
        MapJobRepositoryFactoryBean factoryBean = new MapJobRepositoryFactoryBean();
        factoryBean.setTransactionManager(getTransactionManager());
        return factoryBean;
    }
}
