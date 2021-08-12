package com.curtisnewbie.module.task.scheduling.listeners.internal;

import com.curtisnewbie.module.task.scheduling.JobDelegate;
import com.curtisnewbie.module.task.scheduling.JobUtils;
import com.curtisnewbie.module.task.scheduling.listeners.JobPostExecuteListener;
import com.curtisnewbie.module.task.service.TaskService;
import com.curtisnewbie.module.task.vo.TaskVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * JobPostExecuteListener that update last_run_* result for tasks
 *
 * @author yongjie.zhuang
 */
@Component
public class SaveTaskExecResultPostExecListener implements JobPostExecuteListener {

    @Autowired
    private TaskService taskService;

    @Override
    public void postExecute(JobDelegate.JobExecContext ctx) {
        String result = ctx.getException() != null ?
                "exception " + ctx.getException().getClass().getSimpleName() + " occurred"
                : "success";

        TaskVo tv = JobUtils.getTaskFromJobDataMap(ctx.getJobDetail());
        tv.setLastRunResult(result);
        tv.setLastRunStartTime(ctx.getStartTime());
        tv.setLastRunEndTime(ctx.getEndTime());
        taskService.updateLastRunInfo(tv);
    }
}
