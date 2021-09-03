package com.curtisnewbie.module.task.scheduling.listeners.internal;

import com.curtisnewbie.module.task.constants.NamingConstants;
import com.curtisnewbie.module.task.scheduling.JobDelegate;
import com.curtisnewbie.module.task.scheduling.JobUtils;
import com.curtisnewbie.module.task.scheduling.listeners.JobPostExecuteListener;
import com.curtisnewbie.module.task.service.TaskHistoryService;
import com.curtisnewbie.module.task.vo.TaskHistoryVo;
import com.curtisnewbie.module.task.vo.TaskVo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * <p>
 * PostExecuteListener for task_history
 * </p>
 *
 * @author yongjie.zhuang
 */
@Component
@Slf4j
public class TaskHistoryPostExecListener implements JobPostExecuteListener {

    @Autowired
    private TaskHistoryService taskHistoryService;

    @Override
    public void postExecute(JobDelegate.DelegatedJobContext ctx) {
        final String result = JobUtils.convertResult(ctx);
        final JobDetail jd = ctx.getJobDetail();
        final TaskVo tv = JobUtils.getTask(jd);
        String runBy = JobUtils.getRunBy(jd);
        if (runBy == null) {
            runBy = NamingConstants.SCHEDULER;
            // by default, we consider the job is run by scheduler, unless the user triggers the job manually
        }

        taskHistoryService.saveTaskHistory(TaskHistoryVo.builder()
                .taskId(tv.getId())
                .startTime(ctx.getStartTime())
                .endTime(ctx.getEndTime())
                .runResult(result)
                .runBy(runBy)
                .createTime(new Date())
                .build());

        log.info("Saved task_history for task, id: {}, job_name: {}", tv.getId(), tv.getJobName());
    }
}
