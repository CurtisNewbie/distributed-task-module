package com.curtisnewbie.module.task.scheduling.listeners;

import com.curtisnewbie.module.task.scheduling.JobDelegate;

/**
 * Listener of job's pre-execute events
 * <p>
 * To add a listener, simple create one as spring managed bean, it will be registered automatically
 * <p>
 * For example
 * <pre>
 * {@code
 * @Component
 * public class MyListener implements JobPreExecuteListener {
 *      // ...
 * }
 * }
 * </pre>
 *
 * @author yongjie.zhuang
 */
@FunctionalInterface
public interface JobPreExecuteListener {

    /**
     * Before execution
     */
    void preExecute(JobDelegate.DelegatedJobContext context);
}
