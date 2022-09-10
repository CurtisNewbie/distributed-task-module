package com.curtisnewbie.module.task.vo;

import lombok.*;

import java.util.*;

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
    private Date lastRunStartTime;

    /** the last time this task was finished */
    private Date lastRunEndTime;

    /** app that previously ran this task */
    private String lastRunBy;

    /** result of last execution */
    private String lastRunResult;

}