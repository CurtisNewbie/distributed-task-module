package com.curtisnewbie.module.task.helper;

import com.curtisnewbie.module.task.helper.impl.*;
import com.curtisnewbie.module.task.vo.*;

import java.util.*;

/**
 * Task Helper
 * <p>
 * Helper used by the scheduling functionalities, one may implement this interface as a bean to change how tasks are fetched,
 * updated, and so on
 * <p>
 * The default implementation is {@link LocalDBTaskHelper}, which essentially reads/updates the database connected by the service,
 * and looks for a table called {@code task}
 *
 * @author yongj.zhuang
 */
public interface TaskHelper {

    /**
     * Fetch all tasks
     */
    List<TaskVo> fetchAllTasks(String appGroup);

    /**
     * Update last run info
     */
    void updateLastRunInfo(TaskVo tv);

    /**
     * Check if a task exists by its id
     */
    boolean exists(int taskId);

    /**
     * Mark task as disabled
     *
     * @param taskId        taskId
     * @param lastRunResult last run result
     * @param updateBy      updated by
     */
    void markTaskDisabled(int taskId, String lastRunResult, String updateBy);
}
