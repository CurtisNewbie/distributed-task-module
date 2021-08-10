package com.curtisnewbie.module.task.scheduling;

import com.curtisnewbie.module.task.scheduling.listeners.JobPostExecuteListener;

/**
 * Job that is listenable
 *
 * @author yongjie.zhuang
 */
public interface ListenableJob {

    /**
     * Invoked after execution
     */
    void onPostExecute(JobPostExecuteListener jobPostExecuteListener);

}
