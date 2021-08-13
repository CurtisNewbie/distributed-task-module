package com.curtisnewbie.module.task.service;

import com.curtisnewbie.module.task.vo.TaskVo;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;

import java.text.ParseException;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

/**
 * Service for quartz scheduler
 * <br>
 * <br>
 * This service works for the scheduler in current application node. Tasks are executed by the main node,  so in most
 * cases you should use {@link TaskService} to read / write task entities, and {@link NodeCoordinationService} for
 * coordinating task scheduling between nodes.
 * <br>
 * <br>
 * When you are using RPC frameworks, you don't know which node you are calling, you should be aware of it before you
 * use this service.
 *
 * @author yongjie.zhuang
 */
public interface SchedulerService {

    /**
     * Trigger the job immediately
     * <p>
     * Job must exists so that it can be triggered.
     * </p>
     * <p>
     * Job should only be executed by main node, so do not call this method just to trigger a job, please use {@link
     * NodeCoordinationService#coordinateJobTriggering(TaskVo, String)}}
     * </p>
     * <p>
     * Execution of this job might be affected by {@code TaskEntity#concurrentEnabled}, if it can't be executed
     * concurrently, this method will simply be blocked.
     * </p>
     *
     * @param jobKey jobKey
     */
    void triggerJob(JobKey jobKey) throws SchedulerException;

    /**
     * Remove job
     *
     * @return whether this job is found and deleted
     */
    boolean removeJob(JobKey jobKey) throws SchedulerException;

    /**
     * Schedule job
     *
     * @return next date scheduled
     */
    Date scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException;

    /**
     * Schedule job for the given task entity
     *
     * @param tv task
     * @return next date scheduled
     * @throws SchedulerException
     * @throws ParseException     when the cron expression is invalid
     */
    Date scheduleJob(TaskVo tv) throws SchedulerException, ParseException;

    /**
     * Get job by jobKey
     *
     * @param jobKey jobKey
     */
    Optional<JobDetail> getJob(JobKey jobKey) throws SchedulerException;

    /**
     * Get set of JobKey by group matcher
     */
    Set<JobKey> getJobKeySet(GroupMatcher<JobKey> gm) throws SchedulerException;

    /**
     * Create a trigger for the given task entity
     *
     * @throws ParseException when cron expression is illegal
     */
    Trigger createTrigger(TaskVo tv) throws ParseException;

    /**
     * Remove all jobs
     */
    void removeAllJobs() throws SchedulerException;
}
