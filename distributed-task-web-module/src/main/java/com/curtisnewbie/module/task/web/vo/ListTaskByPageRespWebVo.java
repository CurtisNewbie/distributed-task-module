package com.curtisnewbie.module.task.web.vo;

import com.curtisnewbie.common.vo.PageableVo;
import lombok.Data;

import java.util.List;

/**
 * Response vo for listing tasks in pages
 *
 * @author yongjie.zhuang
 */
@Data
public class ListTaskByPageRespWebVo extends PageableVo {

    private List<TaskWebVo> list;
}