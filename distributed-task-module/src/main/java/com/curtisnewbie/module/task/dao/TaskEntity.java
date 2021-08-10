package com.curtisnewbie.module.task.dao;

import java.util.Date;

/**
 * task
 *
 * @author yongjie.zhuang
 */
public class TaskEntity {
    /** id */
    private Integer id;

    /** job's name */
    private String jobName;

    /** cron expression */
    private String cronExpr;

    /** app group that runs this task */
    private String appGroup;

    /** the last time this task was executed */
    private Date lastRunStartTime;

    /** the last time this task was finished */
    private Date lastRunEndTime;

    /** app that previously ran this task */
    private String lastRunBy;

    /** whether the task is enabled: 0-disabled, 1-enabled */
    private Integer enabled;

    /** whether the task can be executed concurrently: 0-disabled, 1-enabled */
    private Integer concurrentEnabled;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName == null ? null : jobName.trim();
    }

    public String getCronExpr() {
        return cronExpr;
    }

    public void setCronExpr(String cronExpr) {
        this.cronExpr = cronExpr == null ? null : cronExpr.trim();
    }

    public String getAppGroup() {
        return appGroup;
    }

    public void setAppGroup(String appGroup) {
        this.appGroup = appGroup == null ? null : appGroup.trim();
    }

    public Date getLastRunStartTime() {
        return lastRunStartTime;
    }

    public void setLastRunStartTime(Date lastRunStartTime) {
        this.lastRunStartTime = lastRunStartTime;
    }

    public Date getLastRunEndTime() {
        return lastRunEndTime;
    }

    public void setLastRunEndTime(Date lastRunEndTime) {
        this.lastRunEndTime = lastRunEndTime;
    }

    public String getLastRunBy() {
        return lastRunBy;
    }

    public void setLastRunBy(String lastRunBy) {
        this.lastRunBy = lastRunBy == null ? null : lastRunBy.trim();
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Integer getConcurrentEnabled() {
        return concurrentEnabled;
    }

    public void setConcurrentEnabled(Integer concurrentEnabled) {
        this.concurrentEnabled = concurrentEnabled;
    }
}