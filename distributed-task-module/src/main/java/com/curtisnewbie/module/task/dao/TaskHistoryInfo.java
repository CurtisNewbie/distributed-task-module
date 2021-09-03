package com.curtisnewbie.module.task.dao;

import lombok.Data;

import java.util.Date;

/**
 * task history
 *
 * @author yongjie.zhuang
 */
@Data
public class TaskHistoryInfo {
    /** id */
    private Integer id;

    /** task's id */
    private Integer taskId;

    /** job name */
    private String jobName;

    /** start time */
    private Date startTime;

    /** end time */
    private Date endTime;

    /** task triggered by */
    private String runBy;

    /** result of last execution */
    private String runResult;

    /** create time */
    private Date createTime;
}