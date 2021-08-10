package com.curtisnewbie.module.task.service;

import com.curtisnewbie.common.util.EnumUtils;
import com.curtisnewbie.module.redisutil.RedisController;
import com.curtisnewbie.module.task.constants.TaskEnabled;
import com.curtisnewbie.module.task.dao.TaskEntity;
import com.curtisnewbie.module.task.scheduling.JobDetailUtil;
import com.curtisnewbie.module.task.scheduling.TaskJobDetailWrapper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yongjie.zhuang
 */
@Service
@Slf4j
public class NodeCoordinationServiceImpl implements NodeCoordinationService {

    private static final String LOCK_KEY_PREFIX = "task:master:group:";
    private static final int INTERVAL = 1000;
    private static final long DEFAULT_TTL = 10;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;
    private final AtomicBoolean isMainNode = new AtomicBoolean(false);

    @Autowired
    private RedisController redisController;

    @Value("${distributed-task-module.application-group}")
    private String appGroup;

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private TaskService taskService;

    @PostConstruct
    void coordinateThread() {
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
     * Reload the changed tasks and add tasks that are new
     */
    private void refreshScheduledTasks() throws SchedulerException {
        List<TaskEntity> tasks = taskService.selectAll();
        for (TaskEntity te : tasks) {
            Optional<JobDetail> optionalJobDetail = schedulerService.getJob(JobDetailUtil.getJobKey(te));
            if (!optionalJobDetail.isPresent()) {
                TaskEnabled enabled = EnumUtils.parse(te.getEnabled(), TaskEnabled.class);
                Objects.requireNonNull(enabled, "task's field enabled value illegal, unable to parse it");
                // new task, add it into scheduler
                if (enabled.equals(TaskEnabled.ENABLED)) {
                    log.info("Found new task '{}', add it into scheduler", te.getJobName());
                    scheduleJob(te);
                }
            } else {
                // old task, see if it's changed
                JobDetail oldJd = optionalJobDetail.get();
                if (JobDetailUtil.isJobDetailChanged(oldJd, te)) {
                    // changed, delete the old one, and add the new one
                    schedulerService.removeJob(oldJd.getKey());
                    scheduleJob(te);
                }
            }
        }
    }

    private void scheduleJob(TaskEntity te) throws SchedulerException {
        try {
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
