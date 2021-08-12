package com.curtisnewbie.module.task.service;

import com.curtisnewbie.common.util.BeanCopyUtils;
import com.curtisnewbie.common.vo.PagingVo;
import com.curtisnewbie.module.task.constants.TaskEnabled;
import com.curtisnewbie.module.task.dao.TaskEntity;
import com.curtisnewbie.module.task.dao.TaskMapper;
import com.curtisnewbie.module.task.vo.ListTaskByPageReqVo;
import com.curtisnewbie.module.task.vo.ListTaskByPageRespVo;
import com.curtisnewbie.module.task.vo.TaskVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author yongjie.zhuang
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public List<TaskVo> selectAll() {
        return BeanCopyUtils.toTypeList(taskMapper.selectAll(), TaskVo.class);
    }

    @Override
    public PageInfo<ListTaskByPageRespVo> listByPage(ListTaskByPageReqVo param, PagingVo pagingVo) {
        Objects.requireNonNull(param, "TaskEntity shouldn't be null"); Objects.requireNonNull(pagingVo, "Paging param shouldn't be null");
        Objects.requireNonNull(pagingVo.getPage(), "Paging param shouldn't be null");
        Objects.requireNonNull(pagingVo.getLimit(), "Paging param shouldn't be null");

        PageHelper.startPage(pagingVo.getPage(), pagingVo.getLimit());
        PageInfo<TaskEntity> tp = PageInfo.of(taskMapper.selectBy(BeanCopyUtils.toType(param, TaskEntity.class)));
        return BeanCopyUtils.toPageList(tp, ListTaskByPageRespVo.class);
    }

    @Override
    public void updateLastRunInfo(TaskEntity te) {
        taskMapper.updateLastRunInfo(te);
    }

    @Override
    public boolean exists(int taskId) {
        return taskMapper.findOneById(taskId) != null;
    }

    @Override
    public void setTaskDisabled(int taskId) {
        taskMapper.updateEnabled(taskId, TaskEnabled.DISABLED.getValue());
    }

    @Override
    public void setTaskDisabled(int taskId, String result) {
        taskMapper.updateEnabledAndResult(taskId, TaskEnabled.DISABLED.getValue(), result);
    }
}
