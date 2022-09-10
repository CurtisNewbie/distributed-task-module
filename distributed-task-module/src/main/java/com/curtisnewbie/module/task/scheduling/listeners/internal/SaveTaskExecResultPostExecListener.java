package com.curtisnewbie.module.task.scheduling.listeners.internal;

import com.curtisnewbie.module.task.constants.NamingConstants;
import com.curtisnewbie.module.task.helper.*;
import com.curtisnewbie.module.task.scheduling.JobDelegate;
import com.curtisnewbie.module.task.scheduling.JobUtils;
import com.curtisnewbie.module.task.scheduling.listeners.JobPostExecuteListener;
import com.curtisnewbie.module.task.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.*;

/**
 * JobPostExecuteListener that update last_run_* result for tasks
 *
 * @author yongjie.zhuang
 */
@Slf4j
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class SaveTaskExecResultPostExecListener implements JobPostExecuteListener {

    @Autowired
    private TaskHelper taskHelper;

    @Override
    public void postExecute(JobDelegate.DelegatedJobContext ctx) {
        final TaskVo curr = ctx.getTask();

        UpdateLastRunInfoReq update = new UpdateLastRunInfoReq();
        update.setId(curr.getId());

        String runBy = JobUtils.getRunBy(ctx.getJobExecutionContext().getMergedJobDataMap());
        if (runBy == null) {
            runBy = NamingConstants.SCHEDULER;
            // by default, we consider that the job is run by scheduler, unless the user triggers the job manually
        }

        update.setLastRunBy(runBy);
        update.setLastRunResult(JobUtils.extractLastRunResult(ctx));
        update.setLastRunStartTime(ctx.getStartTime());
        update.setLastRunEndTime(ctx.getEndTime());
        taskHelper.updateLastRunInfo(update);

        log.info("Updated execution result for task, id: {}, job_name: {}", curr.getId(), curr.getJobName());
    }
}
