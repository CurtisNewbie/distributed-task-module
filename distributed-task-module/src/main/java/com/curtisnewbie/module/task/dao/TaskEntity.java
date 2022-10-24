package com.curtisnewbie.module.task.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * task
 *
 * @author yongjie.zhuang
 */
@Data
@TableName("task")
public class TaskEntity {

    /** id */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /** job's name */
    @TableField("job_name")
    private String jobName;

    /** name of bean that will be executed */
    @TableField("target_bean")
    private String targetBean;

    /** cron expression */
    @TableField("cron_expr")
    private String cronExpr;

    /** app group that runs this task */
    @TableField("app_group")
    private String appGroup;

    /** the last time this task was executed */
    @TableField("last_run_start_Time")
    private LocalDateTime lastRunStartTime;

    /** the last time this task was finished */
    @TableField("last_run_end_Time")
    private LocalDateTime lastRunEndTime;

    /** app that previously ran this task */
    @TableField("last_run_by")
    private String lastRunBy;

    /** result of last execution */
    @TableField("last_run_result")
    private String lastRunResult;

    /** whether the task is enabled: 0-disabled, 1-enabled */
    @TableField("enabled")
    private Integer enabled;

    /** whether the task can be executed concurrently: 0-disabled, 1-enabled */
    @TableField("concurrent_enabled")
    private Integer concurrentEnabled;

    /** update date */
    @TableField("update_date")
    private LocalDateTime updateDate;

    /** updated by */
    @TableField("update_by")
    private String updateBy;
}