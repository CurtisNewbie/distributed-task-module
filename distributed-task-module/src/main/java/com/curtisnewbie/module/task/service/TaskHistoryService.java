package com.curtisnewbie.module.task.service;

import com.curtisnewbie.common.vo.PageablePayloadSingleton;
import com.curtisnewbie.module.task.vo.ListTaskHistoryByPageReqVo;
import com.curtisnewbie.module.task.vo.ListTaskHistoryByPageRespVo;
import com.curtisnewbie.module.task.vo.TaskHistoryVo;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Service for task_history
 *
 * @author yongjie.zhuang
 */
@Validated
public interface TaskHistoryService {

    /**
     * Save task history
     */
    void saveTaskHistory(@NotNull TaskHistoryVo v);

    /**
     * Find task history in pages
     */
    PageablePayloadSingleton<List<ListTaskHistoryByPageRespVo>> findByPage(@NotNull ListTaskHistoryByPageReqVo param);

}
