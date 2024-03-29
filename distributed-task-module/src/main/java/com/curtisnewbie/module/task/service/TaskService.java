package com.curtisnewbie.module.task.service;

import com.curtisnewbie.common.vo.PageableList;
import com.curtisnewbie.common.vo.PagingVo;
import com.curtisnewbie.module.task.vo.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;
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
@Validated
public interface TaskService {

    /**
     * Select all task entities
     */
    List<TaskVo> selectAll(@NotEmpty String appGroup);

    /**
     * Update record by id
     */
    void updateById(@NotNull UpdateTaskReqVo reqVo);

    /**
     * Select with pagination
     */
    PageableList<ListTaskByPageRespVo> listByPage(@NotNull ListTaskByPageReqVo param, @NotNull PagingVo pagingVo);

    /**
     * Select by id
     */
    TaskVo selectById(int id);

    /**
     * Update last run info
     */
    void updateLastRunInfo(@NotNull TaskVo tv);

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
    void setTaskDisabled(int taskId, @NotNull String result, @NotNull String updateBy);

    /**
     * Update updateBy
     */
    void updateUpdateBy(int taskId, @NotNull String updateBy);

    /**
     * Declare task
     */
    void declareTask(DeclareTaskReq req);
}
