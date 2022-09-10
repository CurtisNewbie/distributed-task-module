package com.curtisnewbie.module.task.plugin;

import com.curtisnewbie.common.preconf.*;
import com.curtisnewbie.common.util.*;
import com.curtisnewbie.module.task.config.*;
import com.curtisnewbie.module.task.vo.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.springframework.http.converter.json.*;
import org.springframework.web.client.*;

import java.util.*;

/**
 * @author yongj.zhuang
 */
@Slf4j
public class DTaskGoTaskHelperTest {

    public static TaskProperties buildTaskProperties() {
        TaskProperties t = new TaskProperties();
        t.setDtaskGoBaseUrl("http://localhost:8082/dtask/remote/");
        return t;
    }

    public static RestTemplate restTemplate() {
        final RestTemplatePreConfigured preConf = new RestTemplatePreConfigured();
        return preConf.restTemplate(preConf.mappingJackson2HttpMessageConverter(JsonUtils.constructsJsonMapper()));
    }

    public static DTaskGoTaskHelper buildHelper() {
        return new DTaskGoTaskHelper(buildTaskProperties(), restTemplate());
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
        r.setLastRunStartTime(new Date());
        r.setLastRunEndTime(new Date());
        r.setLastRunResult("It works great");

        buildHelper().updateLastRunInfo(r);
    }

    @Test
    public void should_check_exists() {
        Assertions.assertTrue(buildHelper().isEnabled(1));
        Assertions.assertFalse(buildHelper().isEnabled(2));
    }

    @Test
    public void should_mark_task_disabled() {
        buildHelper().markTaskDisabled(1, "Invalid Cron Expression", "Scheduler");
    }

}
