package com.curtisnewbie.module.task.scheduling.listeners;

import com.curtisnewbie.module.task.scheduling.JobProxy;

/**
 * Listener of job's post-execute events
 *
 * @author yongjie.zhuang
 */
@FunctionalInterface
public interface JobPostExecuteListener {

    /**
     * After execution
     *
     */
    void postExecute(JobProxy.JobExecContext context);
}
