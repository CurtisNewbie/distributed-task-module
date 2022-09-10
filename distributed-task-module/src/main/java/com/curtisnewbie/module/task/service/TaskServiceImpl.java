package com.curtisnewbie.module.task.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.curtisnewbie.common.util.BeanCopyUtils;
import com.curtisnewbie.common.util.EnumUtils;
import com.curtisnewbie.common.util.PagingUtil;
import com.curtisnewbie.common.vo.PageableList;
import com.curtisnewbie.common.vo.PagingVo;
import com.curtisnewbie.module.task.constants.TaskConcurrentEnabled;
import com.curtisnewbie.module.task.constants.TaskEnabled;
import com.curtisnewbie.module.task.converters.TaskConverter;
import com.curtisnewbie.module.task.dao.TaskEntity;
import com.curtisnewbie.module.task.dao.TaskMapper;
import com.curtisnewbie.module.task.scheduling.JobUtils;
import com.curtisnewbie.module.task.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.curtisnewbie.common.util.PagingUtil.toPageableList;

/**
 * @author yongjie.zhuang
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskConverter taskConverter;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<TaskVo> selectAll(String appGroup) {
        return BeanCopyUtils.toTypeList(taskMapper.selectAll(appGroup), TaskVo.class);
    }

    @Override
    public void updateById(UpdateTaskReqVo vo) {
        Objects.requireNonNull(vo);
        Objects.requireNonNull(vo.getId());

        // null value are not updated, only non-null value are validated
        if (vo.getCronExpr() != null && !JobUtils.isCronExprValid(vo.getCronExpr())) {
            throw new IllegalArgumentException(vo.getCronExpr());
        }
        if (vo.getEnabled() != null) {
            TaskEnabled tce = EnumUtils.parse(vo.getEnabled(), TaskEnabled.class);
            Objects.requireNonNull(tce, "task's field 'enabled' value illegal");
        }
        if (vo.getConcurrentEnabled() != null) {
            TaskConcurrentEnabled tce = EnumUtils.parse(vo.getConcurrentEnabled(), TaskConcurrentEnabled.class);
            Objects.requireNonNull(tce, "task's field 'concurrent_enabled' value illegal");
        }
        TaskEntity param = BeanCopyUtils.toType(vo, TaskEntity.class);
        param.setUpdateDate(new Date());
        taskMapper.updateById(param);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PageableList<ListTaskByPageRespVo> listByPage(@NotNull ListTaskByPageReqVo param, @NotNull PagingVo pagingVo) {
        Objects.requireNonNull(pagingVo.getPage(), "Paging param shouldn't be null");
        Objects.requireNonNull(pagingVo.getLimit(), "Paging param shouldn't be null");

        IPage<TaskEntity> taskEntityIPage = taskMapper.selectBy(PagingUtil.forPage(pagingVo), taskConverter.toDo(param));
        return toPageableList(taskEntityIPage, taskConverter::toListTaskByPageResp);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public TaskVo selectById(int id) {
        return BeanCopyUtils.toType(taskMapper.selectByPrimaryKey(id), TaskVo.class);
    }

    @Override
    public void updateLastRunInfo(TaskVo te) {
        final TaskEntity update = BeanCopyUtils.toType(te, TaskEntity.class);
        update.setUpdateBy("Scheduler");
        update.setUpdateDate(new Date());
        taskMapper.updateLastRunInfo(update);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean exists(int taskId) {
        return taskMapper.findOneById(taskId) != null;
    }

    @Override
    public void setTaskDisabled(int taskId, String result, String updateBy) {
        TaskEntity t = new TaskEntity();
        t.setId(taskId);
        t.setEnabled(TaskEnabled.DISABLED.getValue());
        t.setUpdateDate(new Date());
        t.setUpdateBy(t.getUpdateBy());
        t.setLastRunResult(result);
        taskMapper.updateEnabledAndResult(t);
    }

    @Override
    public void updateUpdateBy(int taskId, String updateBy) {
        TaskEntity te = new TaskEntity();
        te.setId(taskId);
        te.setUpdateBy(updateBy);
        taskMapper.updateUpdateBy(te);
    }
}
