package com.curtisnewbie.module.task.vo;

import lombok.Data;

/**
 * Request vo for listing tasks in pages
 *
 * @author yongjie.zhuang
 */
@Data
public class ListTaskByPageReqVo {

    /** job's name */
    private String jobName;

    /** app group that runs this task */
    private String appGroup;

    /** whether the task is enabled: 0-disabled, 1-enabled */
    private Integer enabled;
}