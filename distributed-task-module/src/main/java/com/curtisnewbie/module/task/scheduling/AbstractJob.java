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

    /**
     * Internal "execute" method to be implemented by a subclass of AbstractJob
     * <p>
     * After job execution, one may update task's lastRunResult, which will then be captured and persisted.
     * For any exception occurred, exception will also be captured, and updated to task's lastRunResult
     */
    protected abstract void executeInternal(TaskVo task) throws JobExecutionException;
}


