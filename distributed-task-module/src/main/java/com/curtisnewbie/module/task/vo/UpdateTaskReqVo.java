package com.curtisnewbie.module.task.vo;

import lombok.Data;

/**
 * Vo for updating task
 *
 * @author yongjie.zhuang
 */
@Data
public class UpdateTaskReqVo {

    /** id */
    private Integer id;

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

    /** updated by */
    private String updateBy;
}