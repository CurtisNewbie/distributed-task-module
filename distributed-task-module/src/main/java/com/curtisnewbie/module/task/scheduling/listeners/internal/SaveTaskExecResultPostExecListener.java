package com.curtisnewbie.module.task.scheduling.listeners.internal;

import com.curtisnewbie.module.task.constants.NamingConstants;
import com.curtisnewbie.module.task.scheduling.JobDelegate;
import com.curtisnewbie.module.task.scheduling.JobUtils;
import com.curtisnewbie.module.task.scheduling.listeners.JobPostExecuteListener;
import com.curtisnewbie.module.task.service.TaskService;
import com.curtisnewbie.module.task.vo.TaskVo;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * JobPostExecuteListener that update last_run_* result for tasks
 *
 * @author yongjie.zhuang
 */
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Component
public class SaveTaskExecResultPostExecListener implements JobPostExecuteListener {

    @Autowired
    private TaskService taskService;

    @Override
    public void postExecute(JobDelegate.JobExecContext ctx) {
        String result = ctx.getException() != null ?
                "exception " + ctx.getException().getClass().getSimpleName() + " occurred"
                : "success";

        JobDetail jd = ctx.getJobDetail();
        TaskVo tv = JobUtils.getTaskFromJobDataMap(jd);

        TaskVo utv = new TaskVo();
        utv.setId(tv.getId());

        String runBy = JobUtils.getRunBy(jd);
        if (runBy == null) {
            runBy = NamingConstants.SCHEDULER;
            // by default, we consider the job is run by scheduler, unless the user triggers the job manually
        }
        utv.setLastRunBy(runBy);
        utv.setLastRunResult(result);
        utv.setLastRunStartTime(ctx.getStartTime());
        utv.setLastRunEndTime(ctx.getEndTime());
        taskService.updateLastRunInfo(utv);
    }
}
