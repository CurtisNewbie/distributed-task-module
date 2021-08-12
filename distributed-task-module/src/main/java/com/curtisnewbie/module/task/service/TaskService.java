package com.curtisnewbie.module.task.service;

import com.curtisnewbie.common.vo.PagingVo;
import com.curtisnewbie.module.task.dao.TaskEntity;
import com.curtisnewbie.module.task.vo.ListTaskByPageReqVo;
import com.curtisnewbie.module.task.vo.TaskVo;
import com.curtisnewbie.module.task.vo.UpdateTaskReqVo;
import com.github.pagehelper.PageInfo;

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
    List<TaskVo> selectAll();

    /**
     * Update record by id
     */
    void updateById(UpdateTaskReqVo reqVo);

    /**
     * Select with pagination
     */
    PageInfo<TaskVo> listByPage(ListTaskByPageReqVo param, PagingVo pagingVo);

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
