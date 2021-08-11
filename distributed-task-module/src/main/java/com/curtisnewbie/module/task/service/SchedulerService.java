package com.curtisnewbie.module.task.service;

import com.curtisnewbie.module.task.dao.TaskEntity;
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
 * <p>
 * This service works for the scheduler in this application instance, tasks are executed by the main node,  so in most
 * cases you should use {@link TaskService} to read / write task entities, and {@link NodeCoordinationService} for
 * coordinating distributed nodes.
 * </p>
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
     * NodeCoordinationService#coordinateJobTriggering(TaskEntity)}
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
     * @param te task entity
     * @return next date scheduled
     * @throws SchedulerException
     * @throws ParseException     when the cron expression is invalid
     */
    Date scheduleJob(TaskEntity te) throws SchedulerException, ParseException;

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
    Trigger createTrigger(TaskEntity te) throws ParseException;

}
