package com.curtisnewbie.module.task.vo;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Vo for task
 *
 * @author yongjie.zhuang
 */
@Data
public class UpdateLastRunInfoReq {

    /** id */
    private Integer id;

    /** the last time this task was executed */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastRunStartTime;

    /** the last time this task was finished */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastRunEndTime;

    /** app that previously ran this task */
    private String lastRunBy;

    /** result of last execution */
    private String lastRunResult;

}