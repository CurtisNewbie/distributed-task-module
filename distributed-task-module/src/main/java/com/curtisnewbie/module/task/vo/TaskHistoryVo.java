package com.curtisnewbie.module.task.vo;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * task history
 *
 * @author yongjie.zhuang
 */
@Data
@NoArgsConstructor
public class TaskHistoryVo {
    /** id */
    private Integer id;

    /** task's id */
    private Integer taskId;

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

    @Builder
    public TaskHistoryVo(Integer id, Integer taskId, Date startTime, Date endTime, String runBy, String runResult,
                         Date createTime) {
        this.id = id;
        this.taskId = taskId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.runBy = runBy;
        this.runResult = runResult;
        this.createTime = createTime;
    }
}