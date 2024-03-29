package com.curtisnewbie.module.task.scheduling.listeners.internal;

import com.curtisnewbie.module.task.constants.NamingConstants;
import com.curtisnewbie.module.task.helper.*;
import com.curtisnewbie.module.task.scheduling.JobDelegate;
import com.curtisnewbie.module.task.scheduling.JobUtils;
import com.curtisnewbie.module.task.scheduling.listeners.JobPostExecuteListener;
import com.curtisnewbie.module.task.vo.TaskHistoryVo;
import com.curtisnewbie.module.task.vo.TaskVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.time.LocalDateTime;

/**
 * <p>
 * PostExecuteListener for task_history
 * </p>
 *
 * @author yongjie.zhuang
 */
// in ascending order, right after TaskHistoryPostExecListener
@Order(value = Ordered.HIGHEST_PRECEDENCE + 1)
@Slf4j
public class TaskHistoryPostExecListener implements JobPostExecuteListener {

    @Autowired
    private TaskHistoryHelper taskHistoryHelper;

    @Override
    public void postExecute(JobDelegate.DelegatedJobContext ctx) {
        final String result = JobUtils.extractLastRunResult(ctx);
        final TaskVo tv = ctx.getTask();
        String runBy = JobUtils.getRunBy(ctx.getJobExecutionContext().getMergedJobDataMap());
        if (runBy == null) {
            runBy = NamingConstants.SCHEDULER;
            // by default, we consider the job is run by scheduler, unless the user triggers the job manually
        }

        final TaskHistoryVo req = TaskHistoryVo.builder()
                .taskId(tv.getId())
                .startTime(ctx.getStartTime())
                .endTime(ctx.getEndTime())
                .runResult(result)
                .runBy(runBy)
                .createTime(LocalDateTime.now())
                .build();
        taskHistoryHelper.saveTaskHistory(req);
        log.info("Saved task_history for task, req: {}", req);
    }
}
