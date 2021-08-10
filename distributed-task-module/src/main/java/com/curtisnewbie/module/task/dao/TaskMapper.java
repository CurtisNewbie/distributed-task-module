package com.curtisnewbie.module.task.dao;

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

    int updateByPrimaryKey(TaskEntity record);
}