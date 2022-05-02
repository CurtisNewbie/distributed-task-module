package com.curtisnewbie.module.task.service;

import com.curtisnewbie.module.redisutil.RedisController;
import com.curtisnewbie.module.task.config.TaskProperties;
import com.curtisnewbie.module.task.scheduling.JobUtils;
import com.curtisnewbie.module.task.scheduling.TriggeredJobKey;
import com.curtisnewbie.module.task.vo.TaskVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author yongjie.zhuang
 */
@Slf4j
@Service
public class NodeCoordinationServiceImpl implements NodeCoordinationService {

    /** uuid that represents current node */
    private static final String UUID = java.util.UUID.randomUUID().toString();
    /** default ttl for master lock key in redis */
    private static final long DEFAULT_TTL = 1;
    /** default timeunit for master lock key in redis */
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;

    @Autowired
    private RedisController redisController;
    @Autowired
    private TaskProperties taskProperties;

    private final AtomicReference<Timer> masterLockRefreshingTimer = new AtomicReference<>();

    @PostConstruct
    void _postConstruct() {
        if (Objects.equals(taskProperties.getAppGroup(), TaskProperties.DEFAULT_APP_GROUP)) {
            log.info("You are using default value for app/scheduling group, consider changing it for you cluster " +
                    "by setting '{}=yourClusterName'", TaskProperties.APP_GROUP_PROP_KEY);
        }
        log.info("Distributed task scheduling for scheduling group: {}, master_node_lock_key: {}, identifier: {}",
                taskProperties.getAppGroup(), getMasterNodeLockKey(), UUID);
    }

    @PreDestroy
    void _preDestroy() {
        cancelMasterLockRefreshingTimer();
    }

    @Override
    public void coordinateJobTriggering(TaskVo tv, String triggerBy) {
        TriggeredJobKey sjk = TriggeredJobKey.fromJobKey(JobUtils.getJobKey(tv));
        sjk.setTriggerBy(triggerBy);
        this.redisController.listLeftPush(getTriggeredJobListKey(), sjk);
    }

    @Override
    public List<TriggeredJobKey> pollTriggeredJobKey(int limit) {
        return redisController.listRightPop(getTriggeredJobListKey(), limit);
    }

    @Override
    public boolean isMaster() {
        final String id = redisController.get(getMasterNodeLockKey());
        return Objects.equals(id, UUID);
    }

    @Override
    public boolean tryBecomeMaster() {
        final boolean hasLock = redisController.setIfNotExists(getMasterNodeLockKey(), UUID, DEFAULT_TTL, DEFAULT_TIME_UNIT);
        if (hasLock) {
            log.info("Elected to be the master node for group: {}", taskProperties.getAppGroup());
            startMasterLockRefreshingTimer();
        } else
            cancelMasterLockRefreshingTimer();
        return hasLock;
    }

    // ----------------------------------------------- private methods --------------------------------------------------

    /** cancel the timer for refreshing master lock if there is one */
    private void cancelMasterLockRefreshingTimer() {
        final Timer timer = masterLockRefreshingTimer.get();
        if (timer != null)
            timer.cancel();
    }

    /**
     * start up a timer to keep refreshing our lock in the background
     */
    private void startMasterLockRefreshingTimer() {
        // create a timer that refreshes the lock for every 5 sec
        final Timer timer = new Timer("masterLockRefresher");
        timer.schedule(new TimerTask() {
                           public void run() {
                               redisController.expire(getMasterNodeLockKey(), DEFAULT_TTL, DEFAULT_TIME_UNIT);
                               log.debug("Refreshing the masterNodeLockKey: '{}'", getMasterNodeLockKey());
                           }
                       },
                0, TimeUnit.SECONDS.toMillis(5));

        // swap the timer ref, if there was one, we cancel it
        final Timer oldTimer = masterLockRefreshingTimer.getAndSet(timer);
        if (oldTimer != null)
            oldTimer.cancel();
    }

    /**
     * Get lock key for being the master node (for redis)
     * <p>
     * Applications are grouped together as a cluster (each cluster is differentiated by its appGroup name {@link
     * TaskProperties#getAppGroup()}), we only try to become the master node of our cluster
     * </p>
     */
    private String getMasterNodeLockKey() {
        return "task:master:group:" + taskProperties.getAppGroup();
    }

    /**
     * Get key for list of triggered job (in redis)
     * <p>
     * Applications are grouped together as a cluster (each cluster is differentiated by its appGroup name {@link
     * TaskProperties#getAppGroup()}), each group has a queue for these triggered jobs
     * </p>
     */
    private String getTriggeredJobListKey() {
        return "task:trigger:group:" + taskProperties.getAppGroup();
    }

}
