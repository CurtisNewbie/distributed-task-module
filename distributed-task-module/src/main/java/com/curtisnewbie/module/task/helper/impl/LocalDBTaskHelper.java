package com.curtisnewbie.module.task.helper.impl;

import com.curtisnewbie.module.task.helper.*;
import com.curtisnewbie.module.task.service.*;
import com.curtisnewbie.module.task.vo.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.stereotype.*;
import org.springframework.util.*;

import java.util.*;

/**
 * Local database based TaskHelper
 *
 * @author yongj.zhuang
 */
@Component
@ConditionalOnMissingBean(TaskHelper.class)
public class LocalDBTaskHelper implements TaskHelper {

    @Autowired
    private TaskService taskService;

    @Override
    public List<TaskVo> fetchAllTasks() {
        return taskService.selectAll();
    }

    @Override
    public void updateLastRunInfo(TaskVo tv) {
        Assert.notNull(tv, "TaskVo == null, unable to update last run info");
        taskService.updateLastRunInfo(tv);
    }

    @Override
    public boolean exists(int taskId) {
        return taskService.exists(taskId);
    }

    @Override
    public void markTaskDisabled(int taskId, String lastRunResult, String updateBy) {
        taskService.setTaskDisabled(taskId, lastRunResult, updateBy);
    }
}
