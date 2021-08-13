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

    @Autowired
    private List<JobPostExecuteListener> jobPostExecuteListenerList;

    @Autowired
    private List<JobPreExecuteListener> jobPreExecuteListenerList;

    /**
     * Register listeners
     *
     * @param job job
     */
    void registerListener(ListenableJob job) {

        for (JobPreExecuteListener listener : jobPreExecuteListenerList)
            job.onPreExecute(listener);

        for (JobPostExecuteListener listener : jobPostExecuteListenerList) {
            job.onPostExecute(listener);
        }
    }
}
