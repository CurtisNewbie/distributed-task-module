package com.curtisnewbie.module.task.service;

import com.curtisnewbie.module.task.vo.ListTaskHistoryByPageReqVo;
import com.curtisnewbie.module.task.vo.TaskHistoryVo;
import com.github.pagehelper.PageInfo;

/**
 * Service for task_history
 *
 * @author yongjie.zhuang
 */
public interface TaskHistoryService {

    /**
     * Save task history
     */
    void saveTaskHistory(TaskHistoryVo v);

    /**
     * Find task history in pages
     */
    PageInfo<TaskHistoryVo> findByPage(ListTaskHistoryByPageReqVo param);

}
