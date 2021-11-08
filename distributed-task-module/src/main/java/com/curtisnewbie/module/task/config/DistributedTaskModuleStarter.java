package com.curtisnewbie.module.task.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.curtisnewbie.module.task.scheduling.MainNodeThread;
import com.curtisnewbie.module.task.service.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Starter for distributed-task-module
 *
 * @author yongjie.zhuang
 */
@Configuration
@ConditionalOnProperty(value = "distributed-task-module.enabled", havingValue = "true", matchIfMissing = true)
public class DistributedTaskModuleStarter {

    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Configuration
    @ConditionalOnProperty(value = "distributed-task-module.scheduling.disabled", havingValue = "false")
    static class SchedulingComponentConfiguration {

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
        public MainNodeThread mainNodeThread() {
            return new MainNodeThread();
        }

        @Bean
        public NodeCoordinationService nodeCoordinationService() {
            return new NodeCoordinationServiceImpl();
        }

        @Bean
        public SchedulerService schedulerService() {
            return new SchedulerServiceImpl();
        }

    }
}
