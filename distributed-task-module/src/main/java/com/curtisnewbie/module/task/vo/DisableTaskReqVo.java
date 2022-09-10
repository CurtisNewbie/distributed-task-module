package com.curtisnewbie.module.task.vo;

import com.fasterxml.jackson.annotation.*;
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
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date updateDate;

    /** updated by */
    private String updateBy;
}