package com.curtisnewbie.module.task.plugin;

import com.curtisnewbie.common.util.*;
import com.curtisnewbie.common.vo.*;
import com.curtisnewbie.module.task.config.*;
import com.curtisnewbie.module.task.helper.*;
import com.curtisnewbie.module.task.vo.*;
import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.client.*;

import java.util.*;

import static com.curtisnewbie.common.util.JsonUtils.*;

/**
 * TaskHelper for dtask-go
 *
 * @author yongj.zhuang
 */
@Slf4j
public class DTaskGoTaskHelper implements TaskHelper {

    @Autowired
    private TaskProperties taskProperties;
    private final ObjectMapper objectMapper = constructsJsonMapper();

    @Override
    public List<TaskVo> fetchAllTasks(String appGroup) {
        RestTemplate rest = new RestTemplate();
        final String payload = rest.getForObject(taskProperties.buildDTaskGoUrl("/task/all"), String.class);
        Result<List<TaskVo>> result = ExceptionUtils.throwIfError(() -> objectMapper.readValue(payload, new TypeReference<Result<List<TaskVo>>>() {
        }));
        result.assertIsOk();
        AssertUtils.notNull(result, "Failed to connect dtask-go");
        return result.getData() != null ? result.getData() : new ArrayList<>();
    }

    @Override
    public void updateLastRunInfo(TaskVo tv) {
        RestTemplate rest = new RestTemplate();
        final Result<?> result = rest.postForObject(taskProperties.buildDTaskGoUrl("/task/update"), tv, Result.class);
        AssertUtils.notNull(result, "Failed to connect dtask-go");
        result.assertIsOk();
    }

    @Override
    public boolean exists(int taskId) {
        RestTemplate rest = new RestTemplate();
        final Result<?> result = rest.getForObject(taskProperties.buildDTaskGoUrl("/task/valid?taskId=" + taskId), Result.class);
        AssertUtils.notNull(result, "Failed to connect dtask-go");
        result.assertIsOk();
        return true;
    }

    @Override
    public void markTaskDisabled(int taskId, String lastRunResult, String updateBy) {
        final DisableTaskReqVo r = new DisableTaskReqVo();
        r.setId(taskId);
        r.setLastRunResult(lastRunResult);
        r.setUpdateBy(updateBy);
        r.setUpdateDate(new Date());

        RestTemplate rest = new RestTemplate();
        final Result<?> result = rest.postForObject(taskProperties.buildDTaskGoUrl("/task/disable"), r, Result.class);
        AssertUtils.notNull(result, "Failed to fetch tasks from dtask-go");
        result.assertIsOk();
    }
}
