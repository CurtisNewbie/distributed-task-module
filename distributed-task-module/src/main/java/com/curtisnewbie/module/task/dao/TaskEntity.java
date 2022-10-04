package com.curtisnewbie.module.task.dao;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * task
 *
 * @author yongjie.zhuang
 */
@Data
public class TaskEntity {
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

    /** the last time this task was executed */
    private LocalDateTime lastRunStartTime;

    /** the last time this task was finished */
    private LocalDateTime lastRunEndTime;

    /** app that previously ran this task */
    private String lastRunBy;

    /** result of last execution */
    private String lastRunResult;

    /** whether the task is enabled: 0-disabled, 1-enabled */
    private Integer enabled;

    /** whether the task can be executed concurrently: 0-disabled, 1-enabled */
    private Integer concurrentEnabled;

    /** update date */
    private LocalDateTime updateDate;

    /** updated by */
    private String updateBy;
}