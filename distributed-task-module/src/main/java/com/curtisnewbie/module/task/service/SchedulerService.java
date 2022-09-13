package com.curtisnewbie.module.task.service;

import com.curtisnewbie.module.task.scheduling.JobUtils;
import com.curtisnewbie.module.task.vo.TaskVo;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
@Validated
public interface SchedulerService {

    /**
     * Trigger the job immediately
     * <p>
     * Job must exist so that it can be triggered.
     * </p>
     * <p>
     * Job should only be executed by main node, so do not call this method just to trigger a job, please use {@link
     * NodeCoordinationService#coordinateJobTriggering(TaskVo, String)}}
     * </p>
     * <p>
     * Execution of this job might be affected by {@code TaskEntity#concurrentEnabled}, if it can't be executed
     * concurrently, the job will simply be blocked.
     * </p>
     *
     * @param jobKey jobKey
     */
    void triggerJob(@NotNull JobKey jobKey) throws SchedulerException;

    /**
     * Create and register trigger that will be triggered for only once
     * <p>
     * The job specified (for the JobKey) will be fired by the created trigger for only once. User of this method may
     * want to remove this 'run-once' trigger by calling {@link #removeTrigger(TriggerKey)}.
     * </p>
     * <p>
     * This kind of 'run-once' trigger can be identified using {@link JobUtils#isRunOnceTrigger(JobDataMap)}
     * </p>
     *
     * @param jobKey jobKey
     */
    void createRunOnceTrigger(@NotNull JobKey jobKey, @NotEmpty String triggeredBy) throws SchedulerException;

    /**
     * Remove a trigger
     */
    void removeTrigger(TriggerKey triggerKey) throws SchedulerException;

    /**
     * Remove job
     *
     * @return whether this job is found and deleted
     */
    boolean removeJob(@NotNull JobKey jobKey) throws SchedulerException;

    /**
     * Add a job, but it's not scheduled
     *
     * @param jobDetail jobDetail
     * @param replace   replace
     */
    void addUnscheduledJob(@NotNull JobDetail jobDetail, boolean replace) throws SchedulerException;

    /**
     * Schedule job
     *
     * @return next date scheduled
     */
    Date scheduleJob(@NotNull JobDetail jobDetail, @NotNull Trigger trigger) throws SchedulerException;

    /**
     * Schedule job for the given task entity
     *
     * @param tv task
     * @return next date scheduled
     * @throws ParseException when the cron expression is invalid
     */
    Date scheduleJob(@NotNull TaskVo tv) throws SchedulerException, ParseException;

    /**
     * Get job by jobKey
     *
     * @param jobKey jobKey
     * @return a copy of jobDetail (modifying it doesn't change the jobDetail in scheduler)
     */
    Optional<JobDetail> getJob(@NotNull JobKey jobKey) throws SchedulerException;

    /**
     * Get set of JobKey by group matcher
     */
    Set<JobKey> getJobKeySet(@NotNull GroupMatcher<JobKey> gm) throws SchedulerException;

    /**
     * Create a trigger for the given task entity
     *
     * @throws ParseException when cron expression is illegal
     */
    Trigger createTrigger(@NotNull TaskVo tv) throws ParseException;

    /**
     * Remove all jobs
     */
    void removeAllJobs() throws SchedulerException;
}
