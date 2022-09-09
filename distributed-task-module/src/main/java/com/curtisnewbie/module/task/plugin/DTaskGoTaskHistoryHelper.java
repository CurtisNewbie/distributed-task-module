package com.curtisnewbie.module.task.plugin;

import com.curtisnewbie.common.util.*;
import com.curtisnewbie.common.vo.*;
import com.curtisnewbie.module.task.config.*;
import com.curtisnewbie.module.task.helper.*;
import com.curtisnewbie.module.task.vo.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.client.*;

/**
 * TaskHistoryHelper for dtask-go
 *
 * @author yongj.zhuang
 */
@Slf4j
public class DTaskGoTaskHistoryHelper implements TaskHistoryHelper {

    @Autowired
    private TaskProperties taskProperties;

    @Override
    public void saveTaskHistory(TaskHistoryVo v) {
        RestTemplate rest = new RestTemplate();
        final Result<?> result = rest.postForObject(taskProperties.buildDTaskGoUrl("/task/history"), v, Result.class);
        AssertUtils.notNull(result, "Failed to fetch tasks from dtask-go");
        result.assertIsOk();
    }
}
