package com.curtisnewbie.module.task.scheduling.listeners.internal;

import com.curtisnewbie.module.task.scheduling.JobDelegate;
import com.curtisnewbie.module.task.scheduling.JobUtils;
import com.curtisnewbie.module.task.scheduling.listeners.JobPostExecuteListener;
import com.curtisnewbie.module.task.service.SchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * PostExecuteListener for removing run-once trigger
 * </p>
 *
 * @author yongjie.zhuang
 * @see SchedulerService#createRunOnceTrigger(JobKey, String)
 * @see JobUtils#isRunOnceTrigger(JobDataMap)
 */
@Slf4j
public class RunOnceTriggerPostExecuteListener implements JobPostExecuteListener {

    @Autowired
    private SchedulerService schedulerService;

    @Override
    public void postExecute(JobDelegate.DelegatedJobContext context) {
        Trigger trigger = context.getJobExecutionContext().getTrigger();
        if (JobUtils.isRunOnceTrigger(trigger.getJobDataMap())) {
            try {
                schedulerService.removeTrigger(trigger.getKey());
            } catch (SchedulerException e) {
                log.error("Unable to remove 'run-once' trigger", e);
            }
        }
    }
}
