package com.curtisnewbie.module.task.vo;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * task history
 *
 * @author yongjie.zhuang
 */
@NoArgsConstructor
@Data
public class ListTaskHistoryByPageRespVo {
    /** id */
    private Integer id;

    /** job name */
    private String jobName;

    /** task's id */
    private Integer taskId;

    /** start time */
    private LocalDateTime startTime;

    /** end time */
    private LocalDateTime endTime;

    /** task triggered by */
    private String runBy;

    /** result of last execution */
    private String runResult;

    @Builder
    public ListTaskHistoryByPageRespVo(Integer id, String jobName, Integer taskId, LocalDateTime startTime, LocalDateTime endTime, String runBy,
                                       String runResult) {
        this.id = id;
        this.jobName = jobName;
        this.taskId = taskId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.runBy = runBy;
        this.runResult = runResult;
    }
}