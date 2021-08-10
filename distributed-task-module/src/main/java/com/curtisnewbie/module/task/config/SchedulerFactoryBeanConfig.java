package com.curtisnewbie.module.task.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Customize {@link SchedulerFactoryBean}
 * <p>
 * Set the customized job factory
 * </p>
 *
 * @author yongjie.zhuang
 * @see ManagedBeanJobFactory
 */
@Configuration
public class SchedulerFactoryBeanConfig implements SchedulerFactoryBeanCustomizer {

    @Autowired
    private ManagedBeanJobFactory managedBeanJobFactory;

    @Override
    public void customize(SchedulerFactoryBean schedulerFactoryBean) {
        schedulerFactoryBean.setJobFactory(managedBeanJobFactory);
    }
}
