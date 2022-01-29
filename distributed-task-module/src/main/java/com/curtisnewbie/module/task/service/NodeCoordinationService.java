package com.curtisnewbie.module.task.service;

import com.curtisnewbie.module.task.scheduling.MasterElectingThread;
import com.curtisnewbie.module.task.scheduling.TriggeredJobKey;
import com.curtisnewbie.module.task.vo.TaskVo;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Service that coordinates between nodes for task scheduling
 *
 * @author yongjie.zhuang
 * @see MasterElectingThread
 */
@Validated
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
    void coordinateJobTriggering(@NotNull TaskVo tv, @NotNull String triggerBy);

    /**
     * Poll triggered jobs' jobKey
     *
     * @param limit limit
     */
    List<TriggeredJobKey> pollTriggeredJobKey(int limit);

    /**
     * Whether we are the master
     */
    boolean isMaster();

    /**
     * Try to become the master, returns immediately if failed, i.e., it's not blocked
     */
    boolean tryBecomeMaster();

}
