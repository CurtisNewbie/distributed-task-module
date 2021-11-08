package com.curtisnewbie.module.task.converters;

import com.curtisnewbie.module.task.dao.TaskEntity;
import com.curtisnewbie.module.task.vo.ListTaskByPageReqVo;
import com.curtisnewbie.module.task.vo.ListTaskByPageRespVo;
import com.curtisnewbie.module.task.vo.TaskVo;
import org.mapstruct.Mapper;

/**
 * Converter for Task
 *
 * @author yongjie.zhuang
 */
@Mapper(componentModel = "spring")
public interface TaskConverter {

    TaskVo toVo(TaskEntity te);

    ListTaskByPageRespVo toListTaskByPageResp(TaskEntity te);

    TaskEntity toDo(TaskVo tv);

    TaskEntity toDo(ListTaskByPageReqVo v);

}
