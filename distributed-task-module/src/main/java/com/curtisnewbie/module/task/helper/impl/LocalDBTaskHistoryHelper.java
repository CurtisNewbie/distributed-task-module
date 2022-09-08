package com.curtisnewbie.module.task.helper.impl;

import com.curtisnewbie.module.task.helper.*;
import com.curtisnewbie.module.task.service.*;
import com.curtisnewbie.module.task.vo.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.stereotype.*;

/**
 * Local database based TaskHistoryHelper
 *
 * @author yongj.zhuang
 */
@Slf4j
@Component
@ConditionalOnMissingBean(TaskHistoryHelper.class)
public class LocalDBTaskHistoryHelper implements TaskHistoryHelper {

    @Autowired
    private TaskHistoryService taskHistoryService;

    @Override
    public void saveTaskHistory(TaskHistoryVo v) {
        if (v == null) return;
        taskHistoryService.saveTaskHistory(v);
    }
}
