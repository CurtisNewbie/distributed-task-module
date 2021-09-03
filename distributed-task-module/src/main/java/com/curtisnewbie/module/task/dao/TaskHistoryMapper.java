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
     * Select id, start_time, end_time, run_by, run_result, create_time
     */
    List<TaskHistoryEntity> findList(TaskHistoryEntity param);
}