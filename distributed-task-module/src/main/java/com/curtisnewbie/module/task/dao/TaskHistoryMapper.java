package com.curtisnewbie.module.task.dao;

import java.util.List;

/**
 * <p>
 * Mapper for task_history
 * </p>
 *
 * @author yongjie.zhuang
 */
public interface TaskHistoryMapper {

    int insert(TaskHistoryEntity record);

    /**
     * Select id, job_name, start_time, end_time, run_by, run_result
     */
    List<TaskHistoryInfo> findList(TaskHistoryInfo param);
}