package com.curtisnewbie.module.task.service;

import com.curtisnewbie.module.task.dao.TaskEntity;

import java.util.List;

/**
 * Service for task
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
}
