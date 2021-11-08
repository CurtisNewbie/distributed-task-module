package com.curtisnewbie.module.task.scheduling.listeners.internal;

import com.curtisnewbie.module.task.constants.NamingConstants;
import com.curtisnewbie.module.task.scheduling.JobDelegate;
import com.curtisnewbie.module.task.scheduling.JobUtils;
import com.curtisnewbie.module.task.scheduling.listeners.JobPostExecuteListener;
import com.curtisnewbie.module.task.service.TaskService;
import com.curtisnewbie.module.task.vo.TaskVo;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class SaveTaskExecResultPostExecListener implements JobPostExecuteListener {

    @Autowired
    private TaskService taskService;

    @Override
    public void postExecute(JobDelegate.DelegatedJobContext ctx) {

        final String result = JobUtils.convertResult(ctx);

        JobDetail jd = ctx.getJobExecutionContext().getJobDetail();
        TaskVo tv = JobUtils.getTask(jd);

        TaskVo utv = new TaskVo();
        utv.setId(tv.getId());

        String runBy = JobUtils.getRunBy(ctx.getJobExecutionContext().getMergedJobDataMap());
        if (runBy == null) {
            runBy = NamingConstants.SCHEDULER;
            // by default, we consider the job is run by scheduler, unless the user triggers the job manually
        }
        utv.setLastRunBy(runBy);
        utv.setLastRunResult(result);
        utv.setLastRunStartTime(ctx.getStartTime());
        utv.setLastRunEndTime(ctx.getEndTime());
        taskService.updateLastRunInfo(utv);

        log.info("Updated execution result for task, id: {}, job_name: {}", tv.getId(), tv.getJobName());
    }
}
