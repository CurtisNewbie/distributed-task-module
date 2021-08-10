package com.curtisnewbie.module.task.service;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import java.util.Date;
import java.util.Optional;

/**
 * Service for quartz scheduler
 *
 * @author yongjie.zhuang
 */
public interface SchedulerService {

    /**
     * Remove job
     *
     * @return whether this job is found and deleted
     */
    boolean removeJob(JobKey jobKey) throws SchedulerException;

    /**
     * Schedule job
     *
     * @return scheduled date
     */
    Date scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException;

    /**
     * Get job by jobKey
     *
     * @param jobKey jobKey
     */
    Optional<JobDetail> getJob(JobKey jobKey) throws SchedulerException;

}
