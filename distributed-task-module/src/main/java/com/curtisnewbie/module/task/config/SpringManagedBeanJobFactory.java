package com.curtisnewbie.module.task.config;

import com.curtisnewbie.module.task.dao.TaskEntity;
import com.curtisnewbie.module.task.exceptions.JobBeanNotFoundException;
import com.curtisnewbie.module.task.scheduling.SpringManagedJob;
import com.curtisnewbie.module.task.scheduling.TaskJobDetailWrapper;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Factory that return job that already exists in spring
 * <p>
 * Instead of creating a new Job instance, we simply return a Job instance that already exists in container
 * </p>
 *
 * @author yongjie.zhuang
 */
@Component
public class SpringManagedBeanJobFactory implements JobFactory {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Job newJob(TriggerFiredBundle triggerFiredBundle, Scheduler scheduler) throws SchedulerException {
        JobDetail jd = triggerFiredBundle.getJobDetail();

        TaskEntity te = (TaskEntity) jd.getJobDataMap().get(TaskJobDetailWrapper.JOD_DATA_MAP_TASK_ENTITY);
        String beanName = te.getTargetBean();
        Objects.requireNonNull(beanName, "Bean name not found from jodDataMap");

        Job job = applicationContext.getBean(beanName, SpringManagedJob.class);
        if (job == null) {
            throw JobBeanNotFoundException.forBeanName(beanName);
        }
        return job;
    }
}
