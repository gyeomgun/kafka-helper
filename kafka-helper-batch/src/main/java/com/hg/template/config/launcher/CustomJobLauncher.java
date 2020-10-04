package com.hg.template.config.launcher;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import java.util.Date;

public class CustomJobLauncher extends SimpleJobLauncher {
    @Override
    public JobExecution run(Job job, JobParameters jobParameters)
            throws JobExecutionAlreadyRunningException, JobRestartException,
            JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        JobParametersBuilder builder = new JobParametersBuilder(jobParameters);
        builder.addParameter("date", new JobParameter((new Date().getTime())));
        return super.run(job, builder.toJobParameters());
    }
}
