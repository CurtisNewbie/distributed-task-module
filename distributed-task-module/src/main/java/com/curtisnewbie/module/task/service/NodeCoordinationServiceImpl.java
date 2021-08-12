package com.curtisnewbie.module.task.service;

import com.curtisnewbie.common.util.EnumUtils;
import com.curtisnewbie.module.redisutil.RedisController;
import com.curtisnewbie.module.task.constants.TaskEnabled;
import com.curtisnewbie.module.task.dao.TaskEntity;
import com.curtisnewbie.module.task.scheduling.JobUtils;
import com.curtisnewbie.module.task.scheduling.SerializableJobKey;
import com.curtisnewbie.module.task.vo.TaskVo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.curtisnewbie.module.task.scheduling.JobUtils.getIdFromJobKey;
import static com.curtisnewbie.module.task.scheduling.JobUtils.getNameFromJobKey;

/**
 * @author yongjie.zhuang
 */
@Service
@Slf4j
public class NodeCoordinationServiceImpl implements NodeCoordinationService {

    private static final int INTERVAL = 500;
    private static final String APP_GROUP_PROP_KEY = "distributed-task-module.application-group";
    private static final String DEFAULT_APP_GROUP = "default";
    private static final long DEFAULT_TTL = 1;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;
    private final AtomicBoolean isMainNode = new AtomicBoolean(false);

    @Autowired
    private RedisController redisController;

    @Value("${" + APP_GROUP_PROP_KEY + ":" + DEFAULT_APP_GROUP + "}")
    private String appGroup;

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private TaskService taskService;

    @PostConstruct
    void coordinateThread() {
        if (Objects.equals(appGroup, DEFAULT_APP_GROUP)) {
            log.info("You are using default value for app/scheduling group, consider changing it for you cluster " +
                    "by setting '{}=yourClusterName'", APP_GROUP_PROP_KEY);
        }

        Thread bg = new Thread(() -> {
            while (true) {
                try {
                    // try to obtain lock
                    boolean isMain = redisController.tryLock(getMainNodeLockKey(), 0, DEFAULT_TTL, DEFAULT_TIME_UNIT);
                    isMainNode.set(isMain);
                    log.debug("Try to become main node, is main node? {}", isMainNode.get());

                    // only the main node of its group can actually run the tasks

                    try {
                        if (isMain) {
                            // refresh jobs, compare scheduled jobs with records in database
                            refreshScheduledTasks();
                            // trigger jobs that need to be executed immediately
                            triggerRunImmediatelyJobs();
                        } else {
                            // if it's no-longer a main node, simply clear the scheduler
                            cleanUpScheduledTasks();
                        }
                    } catch (SchedulerException e) {
                        log.error("Exception occurred while refreshing scheduled tasks from database", e);
                    }

                    Thread.sleep(INTERVAL);
                } catch (InterruptedException e) {
                    // never stop
                    Thread.currentThread().interrupt();
                }
            }
        });
        bg.setDaemon(true);
        bg.start();

        log.info("Started node coordination daemon thread for distributed task scheduling, " +
                        "thread_id: {}, scheduling group: {}, lock_key: {}", bg.getId(),
                appGroup, getMainNodeLockKey());
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

    private void dropNonExistingJobs() throws SchedulerException {
        GroupMatcher<JobKey> any = GroupMatcher.anyJobGroup();
        Set<JobKey> jobKeySet = schedulerService.getJobKeySet(any);
        for (JobKey jk : jobKeySet) {
            if (!taskService.exists(getIdFromJobKey(jk))) {
                log.info("Task '{}' not found in database, removing it from scheduler", getNameFromJobKey(jk));
                schedulerService.removeJob(jk);
            }
        }
    }

    private void scheduleJob(TaskVo te) throws SchedulerException {
        try {
            log.info("Scheduling task: id: '{}', name: '{}' cron_expr: '{}', target_bean: '{}'", te.getId(), te.getJobName(),
                    te.getCronExpr(), te.getTargetBean());
            Date d = schedulerService.scheduleJob(te);
            log.info("Task '{}' scheduled at {}", te.getJobName(), d);
        } catch (ParseException e) {
            log.error("Invalid cron expression found in task, id: '{}', name: '{}', cron_expr: '{}', task has been disabled",
                    te.getId(), te.getJobName(), te.getCronExpr());
            taskService.setTaskDisabled(te.getId(), "Invalid cron expression");
        }
    }

    private void triggerRunImmediatelyJobs() throws SchedulerException {
        // poll at most 30 jobKeys for triggering
        List<SerializableJobKey> serializableJobKeys = redisController.listRightPop(getTriggeredJobListKey(), 30);
        for (SerializableJobKey sjk : serializableJobKeys) {
            JobKey jk = sjk.toJobKey();
            int id = JobUtils.getIdFromJobKey(jk);
            String name = JobUtils.getNameFromJobKey(jk);
            if (schedulerService.getJob(jk).isPresent()) {
                log.info("Triggering job id: '{}', name: '{}'", id, name);
                schedulerService.triggerJob(jk);
            } else {
                log.warn("Job id: '{}', name: '{}' not found, can't be triggered", id, name);
            }
        }
    }

    @Override
    public boolean isMainNode() {
        return isMainNode.get();
    }

    @Override
    public void coordinateJobTriggering(TaskVo tv) {
        this.redisController.listLeftPush(getTriggeredJobListKey(), SerializableJobKey.fromJobKey(JobUtils.getJobKey(tv)));
    }

    /**
     * Get lockKey for mainNode
     * <br>
     * Applications are grouped (different clusters), we only try to become main node of our cluster
     */
    private String getMainNodeLockKey() {
        return "task:master:group:" + appGroup;
    }

    /**
     * Get key for list of triggered job
     * <br>
     * Applications are grouped (different clusters), each group has a queue for these triggered job
     */
    private String getTriggeredJobListKey() {
        return "task:trigger:group:" + appGroup;
    }

}
