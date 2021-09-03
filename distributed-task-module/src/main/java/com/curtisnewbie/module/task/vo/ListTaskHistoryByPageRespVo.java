package com.curtisnewbie.module.task.vo;

import com.curtisnewbie.common.vo.PageableVo;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * task history
 *
 * @author yongjie.zhuang
 */
@Data
public class ListTaskHistoryByPageRespVo extends PageableVo {
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

    @Builder
    public ListTaskHistoryByPageRespVo(Integer id, Integer taskId, Date startTime, Date endTime, String runBy, String runResult) {
        this.id = id;
        this.taskId = taskId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.runBy = runBy;
        this.runResult = runResult;
    }

}