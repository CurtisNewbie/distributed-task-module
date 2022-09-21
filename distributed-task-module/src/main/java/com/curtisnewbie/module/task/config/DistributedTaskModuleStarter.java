package com.curtisnewbie.module.task.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.curtisnewbie.common.preconf.RestTemplatePreConfigured;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

/**
 * Starter for distributed-task-module
 *
 * @author yongjie.zhuang
 */
@ConditionalOnProperty(value = "distributed-task-module.enabled", havingValue = "true", matchIfMissing = true)
public class DistributedTaskModuleStarter {

    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean(TaskHelper.class)
    public TaskHelper localDBTaskHelper() {
        return new LocalDBTaskHelper();
    }

    @Bean
    @ConditionalOnMissingBean(TaskHistoryHelper.class)
    public TaskHistoryHelper localDBTaskHistoryHelper() {
        return new LocalDBTaskHistoryHelper();
    }

    @Configuration
    @ConditionalOnProperty(value = "distributed-task-module.plugin.dtask-go.enabled", havingValue = "true", matchIfMissing = false)
    public static class DTaskGoConfiguration extends RestTemplatePreConfigured {

        @Bean
        @Primary
        public TaskHelper dTaskGoTaskHelper(TaskProperties taskProperties, RestTemplate restTemplate) {
            return new DTaskGoTaskHelper(taskProperties, restTemplate);
        }

        @Bean
        @Primary
        public TaskHistoryHelper dTaskGoTaskHistoryHelper(TaskProperties taskProperties, RestTemplate restTemplate) {
            return new DTaskGoTaskHistoryHelper(taskProperties, restTemplate);
        }
    }

    @Configuration
    @ConditionalOnProperty(value = "distributed-task-module.scheduling.disabled", havingValue = "false", matchIfMissing = true)
    static class SchedulingComponentConfiguration {

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
}
