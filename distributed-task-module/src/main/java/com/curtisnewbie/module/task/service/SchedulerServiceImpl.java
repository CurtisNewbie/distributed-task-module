package com.curtisnewbie.module.task.service;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

/**
 * @author yongjie.zhuang
 */
@Service
public class SchedulerServiceImpl implements SchedulerService {

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Override
    public boolean removeJob(JobKey job) throws SchedulerException {
        return schedulerFactoryBean.getScheduler().deleteJob(job);
    }

    @Override
    public Date scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
        return schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
    }

    @Override
    public Optional<JobDetail> getJob(JobKey jobKey) throws SchedulerException {
        return Optional.ofNullable(schedulerFactoryBean.getScheduler().getJobDetail(jobKey));
    }
}
