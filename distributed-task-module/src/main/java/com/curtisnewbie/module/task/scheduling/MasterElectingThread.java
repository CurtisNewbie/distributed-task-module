package com.curtisnewbie.module.task.scheduling;

import com.curtisnewbie.common.util.EnumUtils;
import com.curtisnewbie.module.task.config.TaskProperties;
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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.curtisnewbie.module.task.scheduling.JobUtils.getIdFromJobKey;

/**
 * Thread for main node coordination
 * <br><br>
 * This bean internally starts a single thread that keeps looping, trying to become the master node. It only does some
 * scheduling operation (e.g., triggering tasks) when it becomes the master node. You should not instantiate it,
 * autowire it, or use it in another thread.
 *
 * @author yongjie.zhuang
 */
@Slf4j
public class MasterElectingThread implements Runnable {

    /** flag to indicate whether application is shutting down */
    private final AtomicBoolean isShutdown = new AtomicBoolean(false);
    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    private Thread backgroundThread;

    @Autowired
    private TaskProperties taskProperties;

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private NodeCoordinationService nodeCoordinationService;

    @PreDestroy
    void shutdownBackgroundThread() {
        log.info("Application shutting down, interrupting daemon thread for master node election");
        isShutdown.set(true);
        if (backgroundThread != null && backgroundThread.isAlive()) {
            backgroundThread.interrupt();
        }
    }

    @PostConstruct
    void startBackgroundThread() {
        // background thread
        backgroundThread = new Thread(this);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
        log.info("Started daemon thread for master node election");
    }

    @Override
    public void run() {
        /*
        this two boolean variables are used as an indicator of changes on being the master, e.g.,
            1) we somehow become the master for current loop, but we are not previously,
            2) or we are no longer the master for current loop, but we previously are.

        If such a change is found, we will need to either refresh all the scheduled tasks
            or clean up the scheduler.
         */
        boolean wasMaster = false;
        boolean isMasterFlagChanged;

        // keep looping until the application is shutting down
        while (!isShutdown.get()) {
            try {

                // try to obtain the lock if we don't have it, but we may still fail
                boolean isMaster;
                if (!(isMaster = nodeCoordinationService.isMaster()))
                    isMaster = nodeCoordinationService.tryBecomeMaster();

                // check if 'isMaster' changed
                isMasterFlagChanged = isMaster == wasMaster;
                wasMaster = isMaster;

                // only the master node of its group can actually run the tasks
                if (isMasterFlagChanged)
                    onMasterFlagChanged(isMaster);

                // sleep for 1 sec on each loop
                Thread.sleep(TimeUnit.SECONDS.toMillis(1));

            } catch (InterruptedException e) {
                // never stop unless the application is shutting down
                if (!isShutdown.get()) Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("Exception occurred and was ignored in MainNodeThread", e);
            }
        }
    }

    // ---------------------------------------- private helper methods -------------------------------------------------

    private void onMasterFlagChanged(final boolean isMaster) {
        try {
            if (isMaster) {
                // making this part async is intentional, so that this thread doesn't block here
                // when the database is exceptionally slow
                singleThreadExecutor.execute(() -> {
                    try {
                        // refresh jobs, compare scheduled jobs with records in database
                        refreshScheduledTasks();
                        // trigger jobs that need to be executed immediately
                        runTriggeredJobs();
                    } catch (SchedulerException e) {
                        log.error("Exception occurred while refreshing scheduled tasks from database", e);
                    }
                });
            } else {
                // if it's no-longer a main node, simply clear the scheduler
                cleanUpScheduledTasks();
            }
        } catch (SchedulerException e) {
            log.error("Exception occurred while refreshing scheduled tasks from database", e);
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
            if (!Objects.equals(tv.getAppGroup(), taskProperties.getAppGroup()))
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
        Set<JobKey> jobKeySet = schedulerService.getJobKeySet(GroupMatcher.anyJobGroup());
        for (JobKey jk : jobKeySet) {
            Optional<JobDetail> jobOpt = schedulerService.getJob(jk);
            if (!jobOpt.isPresent())
                continue;

            if (!taskService.exists(getIdFromJobKey(jk))) {
                log.info("Task_id'{}' not found in database, removing it from scheduler", JobUtils.getIdFromJobKey(jk));
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

            Optional<JobDetail> opt = schedulerService.getJob(jk);
            if (opt.isPresent()) {
                log.info("Triggering job id: '{}'", id);
                schedulerService.createRunOnceTrigger(jk, sjk.getTriggerBy());
            } else {
                log.warn("Job id: '{}' not found, can't be triggered (only enabled job can be triggered)", id);
            }
        }
    }
}