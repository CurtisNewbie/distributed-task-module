package com.curtisnewbie.module.task.scheduling.listeners.internal;

import com.curtisnewbie.module.task.scheduling.JobDelegate;
import com.curtisnewbie.module.task.scheduling.JobUtils;
import com.curtisnewbie.module.task.scheduling.TaskJobDetailWrapper;
import com.curtisnewbie.module.task.scheduling.listeners.JobPostExecuteListener;
import com.curtisnewbie.module.task.service.SchedulerService;
import com.curtisnewbie.module.task.service.TaskService;
import com.curtisnewbie.module.task.vo.TaskVo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Post execute listener for jobs that are triggered manually
 *
 * @author yongjie.zhuang
 */
@Slf4j
@Component
public class TriggeredJobPostExecuteListener implements JobPostExecuteListener {

    @Autowired
    private TaskService taskService;

    @Autowired
    private SchedulerService schedulerService;

    @Override
    public void postExecute(JobDelegate.JobExecContext context) {
        JobDetail jd = context.getJobDetail();
        // job is manually triggered, we need to restore it's JobDataMap by replacing it with the one in database
        if (JobUtils.isJobTriggered(jd)) {
            TaskVo task = taskService.selectById(JobUtils.getIdFromJobKey(jd.getKey()));
            if (task == null)
                return;

            try {
                // todo this just doesn't seem to be a good solution, probably we should just write a jobRunShell Delegate :(
                schedulerService.replaceJobDetail(new TaskJobDetailWrapper(task));
            } catch (SchedulerException e) {
                log.warn("Unable to replace job detail after manual triggering", e);
            }
        }

    }
}
