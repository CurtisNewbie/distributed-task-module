package com.curtisnewbie.module.task.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.curtisnewbie.common.util.EnhancedMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper for task
 *
 * @author yongjie.zhuang
 */
public interface TaskMapper extends EnhancedMapper<TaskEntity> {

    /**
     * Select * by id
     */
    TaskEntity selectByPrimaryKey(Integer id);

    /**
     * Select *
     */
    List<TaskEntity> selectAll(@Param("appGroup") String appGroup);

    /**
     * Update last_run_start_time, last_run_end_time, last_run_result, last_run_by by id
     */
    void updateLastRunInfo(TaskEntity te);

    /**
     * Select 1 by id
     */
    Integer findOneById(@Param("id") int taskId);

    /**
     * Update enabled, result, last_run_result (if non-null), update_by, update_date by id
     */
    void updateEnabledAndResult(TaskEntity taskEntity);

    /**
     * Select * by job_name, enabled, app_group
     */
    IPage<TaskEntity> selectBy(Page p, @Param("p") TaskEntity param);

    /**
     * Update job_name, target_bean, cron_expr, app_group, enabled, concurrent_enabled by id
     */
    void updateOneById(TaskEntity entity);

    /**
     * Update update_by by id
     */
    void updateUpdateBy(TaskEntity te);
}