package com.curtisnewbie.module.task.plugin;

import com.curtisnewbie.common.util.JsonUtils;
import com.curtisnewbie.module.task.config.*;
import com.curtisnewbie.module.task.constants.TaskConcurrentEnabled;
import com.curtisnewbie.module.task.constants.TaskEnabled;
import com.curtisnewbie.module.task.vo.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author yongj.zhuang
 */
@Slf4j
public class DTaskGoTaskHelperTest {

    public static TaskProperties buildTaskProperties() {
        TaskProperties t = new TaskProperties();
        t.setDtaskGoBaseUrl("http://localhost:8083/remote");
        return t;
    }

    public static DTaskGoTaskHelper buildHelper() {
        return new DTaskGoTaskHelper(buildTaskProperties());
    }

    @Test
    public void should_fetch_all_tasks() {
        final List<TaskVo> taskVos = buildHelper().fetchAllTasks("file-service");
        Assertions.assertNotNull(taskVos);
        Assertions.assertTrue(!taskVos.isEmpty());
        log.info("Tasks: {}", taskVos);
    }

    @Test
    public void should_update_last_run_info() {
        UpdateLastRunInfoReq r = new UpdateLastRunInfoReq();
        r.setId(1);
        r.setLastRunBy("Unit Test");
        r.setLastRunStartTime(LocalDateTime.now());
        r.setLastRunEndTime(LocalDateTime.now());
        r.setLastRunResult("It works great");

        buildHelper().updateLastRunInfo(r);
    }

    @Test
    public void should_check_exists() {
        Assertions.assertFalse(buildHelper().isEnabled(2));
        Assertions.assertTrue(buildHelper().isEnabled(1));
    }

    @Test
    public void should_mark_task_disabled() {
        buildHelper().markTaskDisabled(1, "Invalid Cron Expression", "Scheduler");
    }

    @Test
    public void should_declare_task() {
        DeclareTaskReq req = new DeclareTaskReq();
        req.setJobName("TestJob");
        req.setTargetBean("testBean");
        req.setCronExpr("0 0 0/1 ? * *");
        req.setAppGroup("go_group");
        req.setEnabled(TaskEnabled.DISABLED.getValue());
        req.setConcurrentEnabled(TaskConcurrentEnabled.DISABLED.getValue());
        req.setOverridden(true);
        log.info("req: {}", JsonUtils.uwriteValueAsString(req));
        buildHelper().declareTask(req);
    }

}
