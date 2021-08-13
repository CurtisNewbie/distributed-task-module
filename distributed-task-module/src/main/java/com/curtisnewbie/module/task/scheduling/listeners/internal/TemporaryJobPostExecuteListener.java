package com.curtisnewbie.module.task.scheduling.listeners.internal;

import com.curtisnewbie.module.task.scheduling.JobDelegate;
import com.curtisnewbie.module.task.scheduling.JobUtils;
import com.curtisnewbie.module.task.scheduling.listeners.JobPostExecuteListener;
import com.curtisnewbie.module.task.service.SchedulerService;
import com.curtisnewbie.module.task.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Post execute listener for temporary jobs (jobs that are triggered)
 *
 * @author yongjie.zhuang
 */
@Slf4j
@Component
public class TemporaryJobPostExecuteListener implements JobPostExecuteListener {

    @Autowired
    private TaskService taskService;

    @Autowired
    private SchedulerService schedulerService;

    @Override
    public void postExecute(JobDelegate.JobExecContext context) {
        JobDetail jd = context.getJobDetail();
        // this job is a temporary job without triggers (i.e., manually triggered), we just remove it
        if (JobUtils.isTempJob(jd)) {
            try {
                schedulerService.removeJob(jd.getKey());
            } catch (SchedulerException e) {
                log.warn("Unable to remove temporary job", e);
            }
        }

    }
}
