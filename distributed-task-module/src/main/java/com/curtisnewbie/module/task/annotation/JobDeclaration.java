package com.curtisnewbie.module.task.annotation;

import java.lang.annotation.*;

/**
 * Job Declaration
 *
 * @author yongj.zhuang
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface JobDeclaration {

    /** Job name, default to the bean name */
    String name() default "";

    /** Cron expression */
    String cron();

    /** Whether the task is allowed to run concurrently */
    boolean concurrent() default false;

    /** Whether the task is enabled */
    boolean enabled() default true;

    /** Whether this declaration overrides existing configuration */
    boolean overriding() default false;
}
