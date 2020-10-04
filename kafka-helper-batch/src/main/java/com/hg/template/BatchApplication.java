package com.hg.template;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJvmExitCodeMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
public class BatchApplication {
    public static void main(String[] args) {
        System.setProperty("spring.dev.tools.restart.enabled", "false");

        if (args.length < 2) {
            log.error("At least 2 arguments are required");
            System.exit(SimpleJvmExitCodeMapper.JVM_EXITCODE_GENERIC_ERROR);
        }
        ConfigurableApplicationContext appContext = null;
        try {
            SpringApplication app = new SpringApplicationBuilder().properties("spring.batch.job.enabled=false")
                    .sources(Class.forName(args[0]))
                    .web(WebApplicationType.NONE)
                    .build();
            appContext = app.run(args);
            Job job = (Job) appContext.getBean(args[1]);
            JobParametersBuilder builder = new JobParametersBuilder();

            for (int i = 2; i < args.length; i++) {
                String[] param = args[i].split("=");
                builder.addParameter(param[0], new JobParameter(param[1]));
            }

            JobLauncher bean1 = appContext.getBean(JobLauncher.class);
            JobExecution exe = bean1.run(job, builder.toJobParameters());

            if (BatchStatus.FAILED.equals(exe.getStatus())) {
                System.exit(SimpleJvmExitCodeMapper.JVM_EXITCODE_GENERIC_ERROR);
            }
        } catch (Exception e) {
            log.error("Exception occupied during bootstrap", e);
            System.exit(SimpleJvmExitCodeMapper.JVM_EXITCODE_GENERIC_ERROR);
        } finally {
            if (appContext != null) {
                appContext.close();
            }
        }
    }
}
