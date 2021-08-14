package com.curtisnewbie.module.task.scheduling;

import com.curtisnewbie.common.util.EnumUtils;
import com.curtisnewbie.module.task.constants.NamingConstants;
import com.curtisnewbie.module.task.constants.TaskEnabled;
import com.curtisnewbie.module.task.service.NodeCoordinationService;
import com.curtisnewbie.module.task.service.SchedulerService;
import com.curtisnewbie.module.task.service.TaskService;
import com.curtisnewbie.module.task.vo.TaskVo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.curtisnewbie.module.task.scheduling.JobUtils.getIdFromJobKey;
import static com.curtisnewbie.module.task.scheduling.JobUtils.getNameFromJobKey;

/**
 * Thread for main node coordination
 * <br><br>
 * This bean internally starts a single thread looping, trying to become the main node. It only does some scheduling
 * operation when it becomes the main node. Do not instantiate it, autowire it, or use it in another thread.
 *
 * @author yongjie.zhuang
 */
@Component
@Slf4j
public class MainNodeThread implements Runnable {

    private static final int THREAD_SLEEP_INTERVAL = 500;
    private static final String APP_GROUP_PROP_KEY = "distributed-task-module.application-group";
    private static final String DEFAULT_APP_GROUP = "default";
    private final AtomicBoolean isShutdown = new AtomicBoolean(false);
    private Thread backgroundThread;

    @Value("${" + APP_GROUP_PROP_KEY + ":" + DEFAULT_APP_GROUP + "}")
    private String appGroup;

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private NodeCoordinationService nodeCoordinationService;

    @PreDestroy
    void shutdownBackgroundThread() {
        isShutdown.set(true);
        if (backgroundThread.isAlive()) {
            log.info("Application shutting down, interrupting main node daemon thread");
            backgroundThread.interrupt();
        }
    }

    @PostConstruct
    void startMainNodeThread() {
        // background thread
        backgroundThread = new Thread(this);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
        log.info("Started main node daemon thread for distributed task scheduling");
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (isShutdown.get()) {
                    log.info("Application shutting down, terminate thread");
                    return;
                }

                // try to obtain lock
                boolean isMain = nodeCoordinationService.tryToBecomeMainNode();
                log.debug("Try to become main node, is main node? {}", isMain);

                // only the main node of its group can actually run the tasks
                try {
                    if (isMain) {
                        // refresh jobs, compare scheduled jobs with records in database
                        refreshScheduledTasks();
                        // trigger jobs that need to be executed immediately
                        runTriggeredJobs();
                    } else {
                        // if it's no-longer a main node, simply clear the scheduler
                        cleanUpScheduledTasks();
                    }
                } catch (SchedulerException e) {
                    log.error("Exception occurred while refreshing scheduled tasks from database", e);
                }

                Thread.sleep(THREAD_SLEEP_INTERVAL);
            } catch (InterruptedException e) {
                // never stop unless the application is shutting down
                if (!isShutdown.get())
                    Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Clean up scheduled tasks
     */
    private void cleanUpScheduledTasks() throws SchedulerException {
        schedulerService.removeAllJobs();
    }

    /**
     * Reload the changed tasks and add tasks that are new, delete those that no-longer exists in database
     */
    private void refreshScheduledTasks() throws SchedulerException {
        loadJobFromDatabase();
        dropNonExistingJobs();
    }

    private void loadJobFromDatabase() throws SchedulerException {
        List<TaskVo> tasks = taskService.selectAll();
        for (TaskVo tv : tasks) {

            // only when the group matches, this job shall be added
            if (!Objects.equals(tv.getAppGroup(), appGroup))
                continue;

            Optional<JobDetail> optionalJobDetail = schedulerService.getJob(JobUtils.getJobKey(tv));
            if (!optionalJobDetail.isPresent()) {
                TaskEnabled enabled = EnumUtils.parse(tv.getEnabled(), TaskEnabled.class);
                Objects.requireNonNull(enabled, "task's field enabled value illegal, unable to parse it");
                // new task, add it into scheduler
                if (enabled.equals(TaskEnabled.ENABLED)) {
                    log.info("Found new task '{}', add it into scheduler", tv.getJobName(), tv.getCronExpr());
                    scheduleJob(tv);
                }
            } else {
                // old task, see if it's changed
                JobDetail oldJd = optionalJobDetail.get();
                if (JobUtils.isJobDetailChanged(oldJd, tv)) {
                    // changed, delete the old one, and add the new one
                    schedulerService.removeJob(oldJd.getKey());

                    TaskEnabled enabled = EnumUtils.parse(tv.getEnabled(), TaskEnabled.class);
                    Objects.requireNonNull(enabled, "task's field enabled value illegal, unable to parse it");
                    if (!enabled.equals(TaskEnabled.ENABLED)) {
                        log.info("Task '{}' disabled, removed from scheduler", tv.getJobName());
                        continue;
                    }

                    log.info("Detected change on task '{}', reloading", tv.getJobName());
                    scheduleJob(tv);
                }
            }
        }
    }

    private void scheduleJob(TaskVo te) throws SchedulerException {
        try {
            Date d = schedulerService.scheduleJob(te);
            log.info("Task '{}' scheduled at {}", te.getJobName(), d);
        } catch (ParseException e) {
            log.error("Invalid cron expression found in task, id: '{}', name: '{}', cron_expr: '{}', task has been disabled",
                    te.getId(), te.getJobName(), te.getCronExpr());
            taskService.setTaskDisabled(te.getId(), "Invalid cron expression", NamingConstants.SCHEDULER);
        }
    }

    private void dropNonExistingJobs() throws SchedulerException {
        GroupMatcher<JobKey> any = GroupMatcher.anyJobGroup();
        Set<JobKey> jobKeySet = schedulerService.getJobKeySet(any);
        for (JobKey jk : jobKeySet) {
            // temporary jobs are not validated, they are deleted by themselves in listener
            if (JobUtils.isTempJob(schedulerService.getJob(jk).get()))
                continue;

            if (!taskService.exists(getIdFromJobKey(jk))) {
                log.info("Task '{}' not found in database, removing it from scheduler", getNameFromJobKey(jk));
                schedulerService.removeJob(jk);
            }
        }
    }

    /**
     * Run jobs that are manually triggered
     *
     * @see NodeCoordinationService#coordinateJobTriggering(TaskVo, String)
     */
    private void runTriggeredJobs() throws SchedulerException {
        // poll at most 30 jobKeys for triggering
        List<TriggeredJobKey> triggeredJobKeys = nodeCoordinationService.pollTriggeredJobKey(30);

        for (TriggeredJobKey sjk : triggeredJobKeys) {
            JobKey jk = sjk.toJobKey();
            int id = JobUtils.getIdFromJobKey(jk);
            String name = JobUtils.getNameFromJobKey(jk);

            Optional<JobDetail> opt = schedulerService.getJob(jk);
            if (opt.isPresent()) {
                log.info("Triggering job id: '{}', name: '{}'", id, name);

                // create temporary job (not scheduled), and triggers them, once they are done, remove them in listener
                JobDetail tempJob = JobUtils.createTempJob(opt.get());
                JobUtils.setRunBy(tempJob, sjk.getTriggerBy());
                schedulerService.addUnscheduledJob(tempJob, true);
                schedulerService.triggerJob(tempJob.getKey());
            } else {
                log.warn("Job id: '{}', name: '{}' not found, can't be triggered (only enabled job can be triggered)", id, name);
            }
        }
    }
}
