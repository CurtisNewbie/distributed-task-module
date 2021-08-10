package com.curtisnewbie.module.task.scheduling.listeners;

import com.curtisnewbie.module.task.scheduling.JobProxy;

/**
 * Listener of job's post-execute events
 * <p>
 * To add a listener, simple create one as spring managed bean, it will be registered automatically
 * <p>
 * For example
 * <pre>
 * {@code
 * @Component
 * public class MyListener implements JobPostExecuteListener {
 *      // ...
 * }
 * }
 * </pre>
 *
 * @author yongjie.zhuang
 */
@FunctionalInterface
public interface JobPostExecuteListener {

    /**
     * After execution
     */
    void postExecute(JobProxy.JobExecContext context);
}
