package com.curtisnewbie.module.task.exceptions;

import org.quartz.SchedulerException;

/**
 * Target bean for the job is not found
 *
 * @author yongjie.zhuang
 */
public class JobBeanNotFoundException extends SchedulerException {

    public JobBeanNotFoundException() {
    }

    public JobBeanNotFoundException(String msg) {
        super(msg);
    }

    public JobBeanNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public static JobBeanNotFoundException forBeanName(String beanName) {
        return new JobBeanNotFoundException("Bean " + beanName + " not found");
    }

}
