package com.curtisnewbie.module.task.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.curtisnewbie.common.preconf.RestTemplatePreConfigured;
import com.curtisnewbie.module.task.annotation.JobDeclarationReporter;
import com.curtisnewbie.module.task.helper.*;
import com.curtisnewbie.module.task.helper.impl.*;
import com.curtisnewbie.module.task.plugin.DTaskGoTaskHelper;
import com.curtisnewbie.module.task.plugin.DTaskGoTaskHistoryHelper;
import com.curtisnewbie.module.task.scheduling.MasterElectingThread;
import com.curtisnewbie.module.task.scheduling.listeners.internal.RunOnceTriggerPostExecuteListener;
import com.curtisnewbie.module.task.scheduling.listeners.internal.RunningTaskCounterListener;
import com.curtisnewbie.module.task.scheduling.listeners.internal.SaveTaskExecResultPostExecListener;
import com.curtisnewbie.module.task.scheduling.listeners.internal.TaskHistoryPostExecListener;
import com.curtisnewbie.module.task.service.*;
import org.quartz.Job;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Starter for distributed-task-module
 *
 * @author yongjie.zhuang
 */
@ConditionalOnProperty(value = "distributed-task-module.enabled", havingValue = "true", matchIfMissing = true)
public class DistributedTaskModuleStarter extends RestTemplatePreConfigured {

    @Bean
    public JobDeclarationReporter jobDeclarationReporter() {
        return new JobDeclarationReporter();
    }

    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean(TaskHelper.class)
    public TaskHelper taskHelper(TaskProperties taskProperties, RestTemplate restTemplate) {
        if (taskProperties.isDTaskGoPluginEnabled()) return new DTaskGoTaskHelper(taskProperties, restTemplate);
        return new LocalDBTaskHelper();
    }

    @Bean
    @ConditionalOnMissingBean(TaskHistoryHelper.class)
    public TaskHistoryHelper taskHistoryHelper(TaskProperties taskProperties, RestTemplate restTemplate) {
        if (taskProperties.isDTaskGoPluginEnabled()) return new DTaskGoTaskHistoryHelper(taskProperties, restTemplate);
        return new LocalDBTaskHistoryHelper();
    }

    @Bean
    public RunningTaskCounterListener runningTaskCounterListener() {
        return new RunningTaskCounterListener();
    }

    @Bean
    public RunOnceTriggerPostExecuteListener runOnceTriggerPostExecuteListener() {
        return new RunOnceTriggerPostExecuteListener();
    }

    @Bean
    public SaveTaskExecResultPostExecListener saveTaskExecResultPostExecListener() {
        return new SaveTaskExecResultPostExecListener();
    }

    @Bean
    public TaskHistoryPostExecListener taskHistoryPostExecListener() {
        return new TaskHistoryPostExecListener();
    }

    @Bean
    public JobListenerRegistrar jobListenerRegistrar() {
        return new JobListenerRegistrar();
    }

    @Bean
    public ManagedBeanJobFactory managedBeanJobFactory() {
        return new ManagedBeanJobFactory();
    }

    @Bean
    public SchedulerFactoryBeanCustomizer schedulerFactoryBeanConfig() {
        return new SchedulerFactoryBeanConfig();
    }

    @Bean
    public MasterElectingThread mainNodeThread() {
        return new MasterElectingThread();
    }

    @Bean
    public SchedulerService schedulerService() {
        return new SchedulerServiceImpl();
    }
}
