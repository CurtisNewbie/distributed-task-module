package com.curtisnewbie.module.task.vo;

import com.fasterxml.jackson.annotation.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /** end time */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /** task triggered by */
    private String runBy;

    /** result of last execution */
    private String runResult;

    /** create time */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Builder
    public TaskHistoryVo(Integer id, Integer taskId, LocalDateTime startTime, LocalDateTime endTime, String runBy, String runResult,
                         LocalDateTime createTime) {
        this.id = id;
        this.taskId = taskId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.runBy = runBy;
        this.runResult = runResult;
        this.createTime = createTime;
    }
}