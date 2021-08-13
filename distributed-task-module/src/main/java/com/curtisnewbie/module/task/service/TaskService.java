package com.curtisnewbie.module.task.service;

import com.curtisnewbie.common.vo.PagingVo;
import com.curtisnewbie.module.task.vo.ListTaskByPageReqVo;
import com.curtisnewbie.module.task.vo.ListTaskByPageRespVo;
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
    PageInfo<ListTaskByPageRespVo> listByPage(ListTaskByPageReqVo param, PagingVo pagingVo);

    /**
     * Select by id
     */
    TaskVo selectById(int id);

    /**
     * Update last run info
     */
    void updateLastRunInfo(TaskVo tv);

    /**
     * Check if a task exists by its id
     */
    boolean exists(int taskId);

    /**
     * Set task as disabled in database
     *
     * @param taskId   taskId
     * @param updateBy updated by
     * @param result   result (nullable)
     */
    void setTaskDisabled(int taskId, String result, String updateBy);

    /**
     * Update updateBy
     *
     * @param taskId
     * @param updateBy
     */
    void updateUpdateBy(int taskId, String updateBy);
}
