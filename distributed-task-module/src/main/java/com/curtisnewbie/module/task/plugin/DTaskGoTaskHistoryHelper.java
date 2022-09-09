package com.curtisnewbie.module.task.plugin;

import com.curtisnewbie.common.util.*;
import com.curtisnewbie.module.task.config.*;
import com.curtisnewbie.module.task.helper.*;
import com.curtisnewbie.module.task.vo.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
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
        final ResponseEntity<Void> resp = rest.postForEntity(taskProperties.buildDTaskGoUrl("/tasks/history"), v, Void.class);
        AssertUtils.isTrue(resp.getStatusCode().is2xxSuccessful(), "Failed to save task history");
    }
}
