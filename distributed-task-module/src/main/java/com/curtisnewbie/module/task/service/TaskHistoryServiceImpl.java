package com.curtisnewbie.module.task.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.curtisnewbie.common.util.BeanCopyUtils;
import com.curtisnewbie.common.util.PagingUtil;
import com.curtisnewbie.common.vo.PageableList;
import com.curtisnewbie.module.task.converters.TaskHistoryConverter;
import com.curtisnewbie.module.task.dao.TaskHistoryEntity;
import com.curtisnewbie.module.task.dao.TaskHistoryInfo;
import com.curtisnewbie.module.task.dao.TaskHistoryMapper;
import com.curtisnewbie.module.task.vo.ListTaskHistoryByPageReqVo;
import com.curtisnewbie.module.task.vo.ListTaskHistoryByPageRespVo;
import com.curtisnewbie.module.task.vo.TaskHistoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Objects;

import static com.curtisnewbie.common.util.PagingUtil.forPage;

/**
 * @author yongjie.zhuang
 */
@Service
@Transactional
public class TaskHistoryServiceImpl implements TaskHistoryService {

    @Autowired
    private TaskHistoryMapper mapper;
    @Autowired
    private TaskHistoryConverter taskHistoryConverter;

    @Override
    public void saveTaskHistory(TaskHistoryVo v) {
        mapper.insert(BeanCopyUtils.toType(v, TaskHistoryEntity.class));
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PageableList<ListTaskHistoryByPageRespVo> findByPage(@NotNull ListTaskHistoryByPageReqVo param) {
        Objects.requireNonNull(param.getPagingVo());

        IPage<TaskHistoryInfo> p = mapper.findList(forPage(param.getPagingVo()), taskHistoryConverter.toTaskHistoryInfo(param));
        return PagingUtil.toPageableList(p, taskHistoryConverter::toListTaskHistoryByPageRespVo);
    }

}
