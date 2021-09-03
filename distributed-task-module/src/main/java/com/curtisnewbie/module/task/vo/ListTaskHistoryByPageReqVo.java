package com.curtisnewbie.module.task.vo;

import com.curtisnewbie.common.vo.PageableVo;
import lombok.Data;

import java.util.Date;

/**
 * task history
 *
 * @author yongjie.zhuang
 */
@Data
public class ListTaskHistoryByPageReqVo extends PageableVo {

    /** task's id */
    private Integer taskId;

    /** start time */
    private Date startTime;

    /** end time */
    private Date endTime;

    /** task triggered by */
    private String runBy;
}