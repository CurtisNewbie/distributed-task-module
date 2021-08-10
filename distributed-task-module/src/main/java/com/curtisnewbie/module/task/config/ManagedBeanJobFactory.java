package com.curtisnewbie.module.task.config;

import com.curtisnewbie.module.task.dao.TaskEntity;
import com.curtisnewbie.module.task.exceptions.JobBeanNotFoundException;
import com.curtisnewbie.module.task.scheduling.JobDetailUtil;
import com.curtisnewbie.module.task.scheduling.listeners.JobPostExecuteListener;
import com.curtisnewbie.module.task.scheduling.JobProxy;
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
 * Also, before we return it, we create a proxy for these job to better handle their lifecycle
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

    @Override
    public Job newJob(TriggerFiredBundle triggerFiredBundle, Scheduler scheduler) throws SchedulerException {
        JobDetail jd = triggerFiredBundle.getJobDetail();

        TaskEntity te = JobDetailUtil.getTaskEntityFromJobDataMap(jd);
        Objects.requireNonNull(te, "TaskEntity not found in jodDataMap");

        String beanName = te.getTargetBean();
        Objects.requireNonNull(beanName, "Task.target_bean is null");

        Job job = applicationContext.getBean(beanName, Job.class);
        if (job == null) {
            throw JobBeanNotFoundException.forBeanName(beanName);
        }
        // create a proxy of the job to better handle it's lifecycle
        JobProxy jobProxy = new JobProxy(job, jd);
        // register listeners
        registerListener(jobProxy);
        return jobProxy;
    }

    private void registerListener(JobProxy jobProxy) {
        for (JobPostExecuteListener listener : jobPostExecuteListenerList) {
            jobProxy.onPostExecute(listener);
        }
    }
}
