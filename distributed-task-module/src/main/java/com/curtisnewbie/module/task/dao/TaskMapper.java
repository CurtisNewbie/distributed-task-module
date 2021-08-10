package com.curtisnewbie.module.task.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper for task
 *
 * @author yongjie.zhuang
 */
public interface TaskMapper {

    int insert(TaskEntity record);

    TaskEntity selectByPrimaryKey(Integer id);

    List<TaskEntity> selectAll();

    void updateLastRunInfo(TaskEntity te);

    Integer findOneById(@Param("id") int taskId);
}