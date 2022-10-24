package com.curtisnewbie.module.task.plugin;

import com.curtisnewbie.common.util.*;
import com.curtisnewbie.common.vo.*;
import com.curtisnewbie.module.task.config.*;
import com.curtisnewbie.module.task.helper.*;
import com.curtisnewbie.module.task.vo.*;
import com.fasterxml.jackson.core.type.*;
import lombok.extern.slf4j.*;
import org.springframework.web.client.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * TaskHelper for dtask-go
 *
 * @author yongj.zhuang
 */
@Slf4j
public class DTaskGoTaskHelper implements TaskHelper {

    private final TaskProperties taskProperties;
    private final RestTemplate rest;

    public DTaskGoTaskHelper(TaskProperties taskProperties, RestTemplate restTemplate) {
        this.taskProperties = taskProperties;
        this.rest = restTemplate;
    }

    @Override
    public List<TaskVo> fetchAllTasks(String appGroup) {
        final String payload = rest.getForObject(taskProperties.buildDTaskGoUrl("/task/all?appGroup=" + appGroup), String.class);
        Result<List<TaskVo>> result = JsonUtils.ureadValueAsObject(payload, new TypeReference<Result<List<TaskVo>>>() {
        });
        result.assertIsOk();
        AssertUtils.notNull(result, "Failed to connect dtask-go");
        return result.getData() != null ? result.getData() : new ArrayList<>();
    }

    @Override
    public void updateLastRunInfo(UpdateLastRunInfoReq tv) {
        final Result<?> result = rest.postForObject(taskProperties.buildDTaskGoUrl("/task/lastRunInfo/update"), tv, Result.class);
        AssertUtils.notNull(result, "Failed to connect dtask-go");
        result.assertIsOk();
    }

    @Override
    public boolean isEnabled(int taskId) {
        final Result<?> result = rest.getForObject(taskProperties.buildDTaskGoUrl("/task/valid?taskId=" + taskId), Result.class);
        return result.isOk();
    }

    @Override
    public void markTaskDisabled(int taskId, String lastRunResult, String updateBy) {
        final DisableTaskReqVo r = new DisableTaskReqVo();
        r.setId(taskId);
        r.setLastRunResult(lastRunResult);
        r.setUpdateBy(updateBy);
        r.setUpdateDate(LocalDateTime.now());

        final Result<?> result = rest.postForObject(taskProperties.buildDTaskGoUrl("/task/disable"), r, Result.class);
        AssertUtils.notNull(result, "Failed to fetch tasks from dtask-go");
        result.assertIsOk();
    }

    @Override
    public void declareTask(DeclareTaskReq req) {
        // TODO impl this
    }
}
