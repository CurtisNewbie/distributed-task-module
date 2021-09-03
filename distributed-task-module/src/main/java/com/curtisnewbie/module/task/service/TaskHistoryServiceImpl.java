package com.curtisnewbie.module.task.service;

import com.curtisnewbie.common.util.BeanCopyUtils;
import com.curtisnewbie.common.vo.PagingVo;
import com.curtisnewbie.module.task.dao.TaskHistoryEntity;
import com.curtisnewbie.module.task.dao.TaskHistoryMapper;
import com.curtisnewbie.module.task.vo.ListTaskHistoryByPageReqVo;
import com.curtisnewbie.module.task.vo.ListTaskHistoryByPageRespVo;
import com.curtisnewbie.module.task.vo.TaskHistoryVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author yongjie.zhuang
 */
@Service
@Transactional
public class TaskHistoryServiceImpl implements TaskHistoryService {

    @Autowired
    private TaskHistoryMapper mapper;

    @Override
    public void saveTaskHistory(TaskHistoryVo v) {
        mapper.insert(BeanCopyUtils.toType(v, TaskHistoryEntity.class));
    }

    @Override
    public PageInfo<ListTaskHistoryByPageRespVo> findByPage(ListTaskHistoryByPageReqVo param) {
        final PagingVo p = param.getPagingVo();
        Objects.requireNonNull(p);
        PageHelper.startPage(p.getPage(), p.getLimit());
        PageInfo<TaskHistoryEntity> pl = PageInfo.of(
                mapper.findList(BeanCopyUtils.toType(param, TaskHistoryEntity.class))
        );
        return BeanCopyUtils.toPageList(pl, ListTaskHistoryByPageRespVo.class);
    }

}
