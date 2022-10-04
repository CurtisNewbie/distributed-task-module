package com.curtisnewbie.module.task.vo;

import com.curtisnewbie.common.vo.PageableVo;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * task history
 *
 * @author yongjie.zhuang
 */
@Data
public class ListTaskHistoryByPageReqVo extends PageableVo {

    /** task's id */
    private Integer taskId;

    /** task' name */
    private String jobName;

    /** start time */
    private LocalDateTime startTime;

    /** end time */
    private LocalDateTime endTime;

    /** task triggered by */
    private String runBy;
}