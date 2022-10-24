package com.curtisnewbie.module.task.vo;

import lombok.Data;

/**
 * @author yongj.zhuang
 */
@Data
public class DeclareTaskReq {

    /** job's name */
    private String jobName;

    /** name of bean that will be executed */
    private String targetBean;

    /** cron expression */
    private String cronExpr;

    /** app group that runs this task */
    private String appGroup;

    /** whether the task is enabled: 0-disabled, 1-enabled */
    private Integer enabled;

    /** whether the task can be executed concurrently: 0-disabled, 1-enabled */
    private Integer concurrentEnabled;

    /** Whether this declaration overrides existing configuration */
    private Boolean overridden;
}
