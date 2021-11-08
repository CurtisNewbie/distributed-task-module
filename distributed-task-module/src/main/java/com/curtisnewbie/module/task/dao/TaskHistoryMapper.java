package com.curtisnewbie.module.task.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

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
    IPage<TaskHistoryInfo> findList(Page p, @Param("p") TaskHistoryInfo param);
}