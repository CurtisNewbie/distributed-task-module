package com.curtisnewbie.module.task.vo;

import com.fasterxml.jackson.annotation.*;
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
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date startTime;

    /** end time */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** task triggered by */
    private String runBy;

    /** result of last execution */
    private String runResult;

    /** create time */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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