package com.curtisnewbie.module.task.helper.impl;

import com.curtisnewbie.common.util.*;
import com.curtisnewbie.module.task.helper.*;
import com.curtisnewbie.module.task.service.*;
import com.curtisnewbie.module.task.vo.*;
import jodd.bean.*;
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
    public List<TaskVo> fetchAllTasks(String appGroup) {
        return taskService.selectAll(appGroup);
    }

    @Override
    public void updateLastRunInfo(UpdateLastRunInfoReq tv) {
        TaskVo update = BeanCopyUtils.toType(tv, TaskVo.class);

        Assert.notNull(tv, "TaskVo == null, unable to update last run info");
        taskService.updateLastRunInfo(update);
    }

    @Override
    public boolean isEnabled(int taskId) {
        return taskService.exists(taskId);
    }

    @Override
    public void markTaskDisabled(int taskId, String lastRunResult, String updateBy) {
        taskService.setTaskDisabled(taskId, lastRunResult, updateBy);
    }
}
