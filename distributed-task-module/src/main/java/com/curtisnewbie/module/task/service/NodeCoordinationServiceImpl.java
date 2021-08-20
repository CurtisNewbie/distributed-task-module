package com.curtisnewbie.module.task.service;

import com.curtisnewbie.module.redisutil.RedisController;
import com.curtisnewbie.module.task.config.TaskProperties;
import com.curtisnewbie.module.task.scheduling.JobUtils;
import com.curtisnewbie.module.task.scheduling.TriggeredJobKey;
import com.curtisnewbie.module.task.vo.TaskVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author yongjie.zhuang
 */
@Service
@Slf4j
public class NodeCoordinationServiceImpl implements NodeCoordinationService {

    private static final String UUID = java.util.UUID.randomUUID().toString();
    private static final long DEFAULT_TTL = 1;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;

    @Autowired
    private RedisController redisController;

    @Autowired
    private TaskProperties taskProperties;

    @PostConstruct
    void postConstruct() {
        if (!taskProperties.isEnabled())
            return;

        if (Objects.equals(taskProperties.getAppGroup(), TaskProperties.DEFAULT_APP_GROUP)) {
            log.info("You are using default value for app/scheduling group, consider changing it for you cluster " +
                    "by setting '{}=yourClusterName'", TaskProperties.APP_GROUP_PROP_KEY);
        }
        log.info("Distributed task scheduling for scheduling group: {}, main_node_lock_key: {}, identifier: {}",
                taskProperties.getAppGroup(), getMainNodeLockKey(), UUID);
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
    public boolean tryToBecomeMainNode() {
        if (hasMainLock()) {
            // we are still the main node, refresh the expiration
            redisController.expire(getMainNodeLockKey(), DEFAULT_TTL, DEFAULT_TIME_UNIT);
            // pessimistic lock, just in case if the key expires before we attempt to refresh it
            return hasMainLock();
        }
        return tryMainLock();
    }

    private boolean hasMainLock() {
        String id = redisController.get(getMainNodeLockKey());
        if (Objects.equals(id, UUID))
            return true;
        return false;
    }

    private boolean tryMainLock() {
        return redisController.setIfNotExists(getMainNodeLockKey(), UUID, DEFAULT_TTL, DEFAULT_TIME_UNIT);
    }

    /**
     * Get lockKey for mainNode
     * <br>
     * Applications are grouped (in different clusters), we only try to become main node of our cluster
     */
    private String getMainNodeLockKey() {
        return "task:master:group:" + taskProperties.getAppGroup();
    }

    /**
     * Get key for list of triggered job
     * <br>
     * Applications are grouped (different clusters), each group has a queue for these triggered job
     */
    private String getTriggeredJobListKey() {
        return "task:trigger:group:" + taskProperties.getAppGroup();
    }

}
