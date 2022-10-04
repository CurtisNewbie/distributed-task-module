package com.curtisnewbie.module.task.dao;

import lombok.Data;

import java.time.LocalDateTime;


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
    private LocalDateTime startTime;

    /** end time */
    private LocalDateTime endTime;

    /** task triggered by */
    private String runBy;

    /** result of last execution */
    private String runResult;

    /** create time */
    private LocalDateTime createTime;
}