package com.curtisnewbie.module.task.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper for task
 *
 * @author yongjie.zhuang
 */
public interface TaskMapper {

    /**
     * Insert
     */
    int insert(TaskEntity record);

    /**
     * Select * by id
     */
    TaskEntity selectByPrimaryKey(Integer id);

    /**
     * Select *
     */
    List<TaskEntity> selectAll();

    /**
     * Update last_run_start_time, last_run_end_time, last_run_result, last_run_by by id
     */
    void updateLastRunInfo(TaskEntity te);

    /**
     * Select 1 by id
     */
    Integer findOneById(@Param("id") int taskId);

    /**
     * Update enabled by id
     */
    void updateEnabled(@Param("id") int taskId, @Param("enabled") int enabled);

    /**
     * Update enabled, result by id
     */
    void updateEnabledAndResult(@Param("id") int taskId, @Param("enabled") int enabled, @Param("result") String result);

    /**
     * Select * by job_name, enabled, app_group
     */
    List<TaskEntity> selectBy(TaskEntity param);

    /**
     * Update job_name, target_bean, cron_expr, app_group, enabled, concurrent_enabled by id
     */
    void updateById(TaskEntity entity);
}