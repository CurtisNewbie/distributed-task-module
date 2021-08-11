package com.curtisnewbie.module.task.config;

import com.curtisnewbie.module.task.dao.TaskEntity;
import com.curtisnewbie.module.task.exceptions.JobBeanNotFoundException;
import com.curtisnewbie.module.task.scheduling.JobUtils;
import com.curtisnewbie.module.task.scheduling.listeners.JobPostExecuteListener;
import com.curtisnewbie.module.task.scheduling.JobDelegate;
import com.curtisnewbie.module.task.scheduling.listeners.JobPreExecuteListener;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Job Factory with managed beans
 * <p>
 * Instead of creating a new Job instance, we simply return an autowired bean that already exists in spring container.
 * Also, before we return it, we create a delegate for these job to better handle their lifecycle
 * </p>
 *
 * @author yongjie.zhuang
 * @see
 */
@Component
public class ManagedBeanJobFactory implements JobFactory {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private List<JobPostExecuteListener> jobPostExecuteListenerList;

    @Autowired
    private List<JobPreExecuteListener> jobPreExecuteListenerList;

    @Override
    public Job newJob(TriggerFiredBundle triggerFiredBundle, Scheduler scheduler) throws SchedulerException {
        JobDetail jd = triggerFiredBundle.getJobDetail();

        TaskEntity te = JobUtils.getTaskEntityFromJobDataMap(jd);
        Objects.requireNonNull(te, "TaskEntity not found in jodDataMap");

        String beanName = te.getTargetBean();
        Objects.requireNonNull(beanName, "Task.target_bean is null");

        Job job = applicationContext.getBean(beanName, Job.class);
        if (job == null) {
            throw JobBeanNotFoundException.forBeanName(beanName);
        }
        // create a delegate of the job to better handle it's lifecycle
        JobDelegate jobDelegate = new JobDelegate(job, jd);
        // register listeners
        registerListener(jobDelegate);
        return jobDelegate;
    }

    private void registerListener(JobDelegate jobDelegate) {
        for (JobPreExecuteListener listener : jobPreExecuteListenerList)
            jobDelegate.onPreExecute(listener);

        for (JobPostExecuteListener listener : jobPostExecuteListenerList) {
            jobDelegate.onPostExecute(listener);
        }
    }
}
