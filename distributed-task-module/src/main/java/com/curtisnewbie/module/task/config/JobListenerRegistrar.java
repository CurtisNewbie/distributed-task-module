package com.curtisnewbie.module.task.config;

import com.curtisnewbie.module.task.scheduling.ListenableJob;
import com.curtisnewbie.module.task.scheduling.listeners.JobPostExecuteListener;
import com.curtisnewbie.module.task.scheduling.listeners.JobPreExecuteListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Registrar of job's listeners
 *
 * @author yongjie.zhuang
 */
@Component
public class JobListenerRegistrar {

    @Autowired(required = false)
    private List<JobPostExecuteListener> postExecList;

    @Autowired(required = false)
    private List<JobPreExecuteListener> preExecList;

    /**
     * Register listeners
     *
     * @param job job
     */
    void registerListener(ListenableJob job) {

        if (preExecList != null)
            for (JobPreExecuteListener listener : preExecList)
                job.onPreExecute(listener);

        if (postExecList != null)
            for (JobPostExecuteListener listener : postExecList) {
                job.onPostExecute(listener);
            }
    }
}
