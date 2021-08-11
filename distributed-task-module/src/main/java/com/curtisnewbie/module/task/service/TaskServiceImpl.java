package com.curtisnewbie.module.task.service;

import com.curtisnewbie.module.task.constants.TaskEnabled;
import com.curtisnewbie.module.task.dao.TaskEntity;
import com.curtisnewbie.module.task.dao.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author yongjie.zhuang
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public List<TaskEntity> selectAll() {
        return taskMapper.selectAll();
    }

    @Override
    public void updateLastRunInfo(TaskEntity te) {
        taskMapper.updateLastRunInfo(te);
    }

    @Override
    public boolean exists(int taskId) {
        return taskMapper.findOneById(taskId) != null;
    }

    @Override
    public void disableTask(int taskId) {
        taskMapper.updateEnabled(taskId, TaskEnabled.DISABLED.getValue());
    }

    @Override
    public void disableTask(int taskId, String result) {
        taskMapper.updateEnabledAndResult(taskId, TaskEnabled.DISABLED.getValue(), result);
    }
}
