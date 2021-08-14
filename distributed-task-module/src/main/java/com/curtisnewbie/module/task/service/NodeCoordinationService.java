package com.curtisnewbie.module.task.service;

import com.curtisnewbie.module.task.scheduling.TriggeredJobKey;
import com.curtisnewbie.module.task.vo.TaskVo;

import java.util.List;

/**
 * Service that coordinates between nodes for task scheduling
 *
 * @author yongjie.zhuang
 * @see com.curtisnewbie.module.task.scheduling.MainNodeThread
 */
public interface NodeCoordinationService {

    /**
     * Coordinate between nodes to trigger a job immediately
     * <br>
     * This method doesn't try to run the job immediately, rather, it let the main node to trigger this job because jobs
     * are loaded in main node as well.
     *
     * @param triggerBy triggered by
     * @param tv        task
     */
    void coordinateJobTriggering(TaskVo tv, String triggerBy);

    /**
     * Poll triggered jobs' jobKey
     *
     * @param limit limit
     */
    List<TriggeredJobKey> pollTriggeredJobKey(int limit);

    /**
     * Try to become the main node
     *
     * @return true if current node is now the main node else false
     */
    boolean tryToBecomeMainNode() throws InterruptedException;

}
