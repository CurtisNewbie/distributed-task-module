package com.curtisnewbie.module.task.scheduling;

import com.curtisnewbie.module.task.vo.TaskVo;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Abstract implementation of Job
 *
 * @author yongjie.zhuang
 */
public abstract class AbstractJob implements Job {

    @Override
    public final void execute(JobExecutionContext context) throws JobExecutionException {
        executeInternal(JobUtils.getTask(context.getJobDetail()));
    }

    protected abstract void executeInternal(TaskVo task) throws JobExecutionException;
}


