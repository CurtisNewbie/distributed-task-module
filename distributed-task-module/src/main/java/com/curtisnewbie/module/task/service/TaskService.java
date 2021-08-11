package com.curtisnewbie.module.task.service;

import com.curtisnewbie.module.task.dao.TaskEntity;

import java.util.List;

/**
 * Service for task
 * <p>
 * This service is used for inserting or updating data, for changing the task scheduled, use {@link SchedulerService}
 * instead.
 * </p>
 *
 * @author yongjie.zhuang
 */
public interface TaskService {

    /**
     * Select all task entities
     */
    List<TaskEntity> selectAll();

    /**
     * Update last run info
     */
    void updateLastRunInfo(TaskEntity te);

    /**
     * Check if a task exists by its id
     */
    boolean exists(int taskId);

    /**
     * Set task as disabled in database
     *
     * @param taskId taskId
     */
    void setTaskDisabled(int taskId);

    /**
     * Set task as disabled in database
     *
     * @param taskId taskId
     */
    void setTaskDisabled(int taskId, String result);
}
