package com.curtisnewbie.module.task.plugin;

import com.curtisnewbie.common.util.*;
import com.curtisnewbie.module.task.vo.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static com.curtisnewbie.module.task.plugin.DTaskGoTaskHelperTest.*;

/**
 * @author yongj.zhuang
 */
@Slf4j
public class DTaskGoTaskHistoryHelperTest {

    public static DTaskGoTaskHistoryHelper buildHelper() {
        return new DTaskGoTaskHistoryHelper(buildTaskProperties());
    }

    @Test
    public void should_record_task_history() {
        final TaskHistoryVo t = TaskHistoryVo.builder()
                .taskId(1)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .runBy("MX Scheduler")
                .runResult("Very Good :D")
                .build();
        final String s = JsonUtils.uwritePretty(t);
        buildHelper().saveTaskHistory(t);
    }
}
