package com.curtisnewbie.module.task.service;

import com.curtisnewbie.common.util.BeanCopyUtils;
import com.curtisnewbie.common.util.EnumUtils;
import com.curtisnewbie.common.vo.PagingVo;
import com.curtisnewbie.module.task.constants.TaskConcurrentEnabled;
import com.curtisnewbie.module.task.constants.TaskEnabled;
import com.curtisnewbie.module.task.dao.TaskEntity;
import com.curtisnewbie.module.task.dao.TaskMapper;
import com.curtisnewbie.module.task.vo.ListTaskByPageReqVo;
import com.curtisnewbie.module.task.vo.TaskVo;
import com.curtisnewbie.module.task.vo.UpdateTaskReqVo;
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
    public void updateById(UpdateTaskReqVo vo) {
        Objects.requireNonNull(vo);
        Objects.requireNonNull(vo.getId());
        // null value are not updated, only non-null value are validated
        if (vo.getEnabled() != null) {
            TaskEnabled tce = EnumUtils.parse(vo.getEnabled(), TaskEnabled.class);
            Objects.requireNonNull(tce, "task's field 'enabled' value illegal");
        }
        if (vo.getConcurrentEnabled() != null) {
            TaskConcurrentEnabled tce = EnumUtils.parse(vo.getConcurrentEnabled(), TaskConcurrentEnabled.class);
            Objects.requireNonNull(tce, "task's field 'concurrent_enabled' value illegal");
        }
        taskMapper.updateById(BeanCopyUtils.toType(vo, TaskEntity.class));
    }

    @Override
    public PageInfo<TaskVo> listByPage(ListTaskByPageReqVo param, PagingVo pagingVo) {
        Objects.requireNonNull(param, "TaskEntity shouldn't be null");
        Objects.requireNonNull(pagingVo, "Paging param shouldn't be null");
        Objects.requireNonNull(pagingVo.getPage(), "Paging param shouldn't be null");
        Objects.requireNonNull(pagingVo.getLimit(), "Paging param shouldn't be null");

        PageHelper.startPage(pagingVo.getPage(), pagingVo.getLimit());
        PageInfo<TaskEntity> tp = PageInfo.of(taskMapper.selectBy(BeanCopyUtils.toType(param, TaskEntity.class)));
        return BeanCopyUtils.toPageList(tp, TaskVo.class);
    }

    @Override
    public TaskVo selectById(int id) {
        return BeanCopyUtils.toType(taskMapper.selectByPrimaryKey(id), TaskVo.class);
    }

    @Override
    public void updateLastRunInfo(TaskVo te) {
        taskMapper.updateLastRunInfo(BeanCopyUtils.toType(te, TaskEntity.class));
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
