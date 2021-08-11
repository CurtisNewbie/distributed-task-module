package com.curtisnewbie.module.task.service;

import com.curtisnewbie.common.util.EnumUtils;
import com.curtisnewbie.module.redisutil.RedisController;
import com.curtisnewbie.module.task.constants.TaskEnabled;
import com.curtisnewbie.module.task.dao.TaskEntity;
import com.curtisnewbie.module.task.scheduling.JobDetailUtil;
import com.curtisnewbie.module.task.scheduling.TaskJobDetailWrapper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.curtisnewbie.module.task.scheduling.JobDetailUtil.getIdFromJobKey;
import static com.curtisnewbie.module.task.scheduling.JobDetailUtil.getNameFromJobKey;

/**
 * @author yongjie.zhuang
 */
@Service
@Slf4j
public class NodeCoordinationServiceImpl implements NodeCoordinationService {

    private static final String LOCK_KEY_PREFIX = "task:master:group:";
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
                    boolean isMain = redisController.tryLock(getLockKey(), 0, DEFAULT_TTL, DEFAULT_TIME_UNIT);
                    isMainNode.set(isMain);
                    log.debug("Try to become main node, is main node? {}", isMainNode.get());

                    // only the main node of its group can actually run the tasks
                    if (isMain) {
                        try {
                            refreshScheduledTasks();
                        } catch (SchedulerException e) {
                            log.error("Exception occurred while refreshing scheduled tasks from database", e);
                        }
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
                appGroup, getLockKey());
    }

    /**
     * Reload the changed tasks and add tasks that are new, delete those that no-longer exists in database
     */
    private void refreshScheduledTasks() throws SchedulerException {
        loadJobFromDatabase();
        dropNonExistingJobs();
    }

    private void loadJobFromDatabase() throws SchedulerException {
        List<TaskEntity> tasks = taskService.selectAll();
        for (TaskEntity te : tasks) {

            // only when the group matches, this job shall be added
            if (!Objects.equals(te.getAppGroup(), appGroup))
                continue;

            Optional<JobDetail> optionalJobDetail = schedulerService.getJob(JobDetailUtil.getJobKey(te));
            if (!optionalJobDetail.isPresent()) {
                TaskEnabled enabled = EnumUtils.parse(te.getEnabled(), TaskEnabled.class);
                Objects.requireNonNull(enabled, "task's field enabled value illegal, unable to parse it");
                // new task, add it into scheduler
                if (enabled.equals(TaskEnabled.ENABLED)) {
                    log.info("Found new task '{}', add it into scheduler", te.getJobName(), te.getCronExpr());
                    scheduleJob(te);
                }
            } else {
                // old task, see if it's changed
                JobDetail oldJd = optionalJobDetail.get();
                if (JobDetailUtil.isJobDetailChanged(oldJd, te)) {
                    // changed, delete the old one, and add the new one
                    schedulerService.removeJob(oldJd.getKey());

                    TaskEnabled enabled = EnumUtils.parse(te.getEnabled(), TaskEnabled.class);
                    Objects.requireNonNull(enabled, "task's field enabled value illegal, unable to parse it");
                    if (!enabled.equals(TaskEnabled.ENABLED)) {
                        log.info("Task '{}' disabled, removed from scheduler", te.getJobName());
                        continue;
                    }

                    log.info("Detected change on task '{}', reloading", te.getJobName());
                    scheduleJob(te);
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


    private void scheduleJob(TaskEntity te) throws SchedulerException {
        try {
            log.info("Scheduling task: '{}' cron_expr: '{}', target_bean: '{}'", te.getJobName(), te.getCronExpr(), te.getTargetBean());
            Date d = schedulerService.scheduleJob(new TaskJobDetailWrapper(te), createTrigger(te));
            log.info("Task '{}' scheduled at {}", te.getJobName(), d);
        } catch (ParseException e) {
            log.error("Invalid cron expression found in task.id: " + te.getId(), e);
        }
    }

    @Override
    public boolean isMainNode() {
        return isMainNode.get();
    }

    private String getLockKey() {
        // applications are grouped (different clusters), we only try to become main node of our cluster
        return LOCK_KEY_PREFIX + appGroup;
    }

    private Trigger createTrigger(TaskEntity te) throws ParseException {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setName(te.getJobName());
        factoryBean.setStartTime(new Date());
        factoryBean.setCronExpression(te.getCronExpr());
        factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }
}
