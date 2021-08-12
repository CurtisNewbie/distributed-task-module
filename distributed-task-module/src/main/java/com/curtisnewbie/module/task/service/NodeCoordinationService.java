package com.curtisnewbie.module.task.service;

import com.curtisnewbie.module.task.vo.TaskVo;

/**
 * Service that coordinates between nodes for task scheduling
 * <p>
 * Only the main node can run tasks, and each cluster (applications that use the same app group name) has a main node.
 * </p>
 *
 * @author yongjie.zhuang
 */
public interface NodeCoordinationService {

    /**
     * Whether current node is a main node (coordinator)
     */
    boolean isMainNode();

    /**
     * Coordinate between nodes to trigger a job immediately
     * <br>
     * This method doesn't try to run the job immediately, rather, it let the main node to trigger this job because jobs
     * are loaded in main node as well.
     *
     * @param tv task
     */
    void coordinateJobTriggering(TaskVo tv);

}
