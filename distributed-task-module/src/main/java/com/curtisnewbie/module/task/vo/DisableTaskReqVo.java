package com.curtisnewbie.module.task.vo;

import lombok.*;

import java.util.*;

/**
 * @author yongjie.zhuang
 */
@Data
public class DisableTaskReqVo {

    /** id */
    private Integer id;

    /** result of last execution */
    private String lastRunResult;

    /** update date */
    private Date updateDate;

    /** updated by */
    private String updateBy;
}