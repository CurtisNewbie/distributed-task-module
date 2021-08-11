package com.curtisnewbie.module.task.scheduling;

import com.curtisnewbie.module.task.scheduling.listeners.JobPostExecuteListener;
import com.curtisnewbie.module.task.scheduling.listeners.JobPreExecuteListener;

/**
 * Job that is listenable
 *
 * @author yongjie.zhuang
 */
public interface ListenableJob {

    /**
     * Invoked before execution
     */
    void onPreExecute(JobPreExecuteListener jobPreExecuteListener);

    /**
     * Invoked after execution
     */
    void onPostExecute(JobPostExecuteListener jobPostExecuteListener);

}
