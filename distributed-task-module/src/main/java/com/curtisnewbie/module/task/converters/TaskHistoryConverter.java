package com.curtisnewbie.module.task.converters;

import com.curtisnewbie.module.task.dao.TaskHistoryInfo;
import com.curtisnewbie.module.task.vo.ListTaskHistoryByPageReqVo;
import com.curtisnewbie.module.task.vo.ListTaskHistoryByPageRespVo;
import org.mapstruct.Mapper;

/**
 * Converter for TaskHistory
 *
 * @author yongjie.zhuang
 */
@Mapper(componentModel = "spring")
public interface TaskHistoryConverter {

    TaskHistoryInfo toTaskHistoryInfo(ListTaskHistoryByPageReqVo reqVo);

    ListTaskHistoryByPageRespVo toListTaskHistoryByPageRespVo(TaskHistoryInfo th);
}
