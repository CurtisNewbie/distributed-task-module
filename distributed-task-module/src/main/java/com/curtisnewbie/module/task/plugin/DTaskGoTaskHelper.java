package com.curtisnewbie.module.task.plugin;

import com.curtisnewbie.common.util.*;
import com.curtisnewbie.module.task.config.*;
import com.curtisnewbie.module.task.helper.*;
import com.curtisnewbie.module.task.vo.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.client.*;

import java.util.*;

/**
 * TaskHelper for dtask-go
 *
 * @author yongj.zhuang
 */
@Slf4j
public class DTaskGoTaskHelper implements TaskHelper {

    @Autowired
    private TaskProperties taskProperties;

    @Override
    public List<TaskVo> fetchAllTasks(String appGroup) {
        RestTemplate rest = new RestTemplate();
        final TaskVo[] tasks = rest.getForObject(taskProperties.buildDTaskGoUrl("/tasks/all"), TaskVo[].class);
        if (tasks == null) return new ArrayList<>();
        return Arrays.asList(tasks);
    }

    @Override
    public void updateLastRunInfo(TaskVo tv) {
        RestTemplate rest = new RestTemplate();
        final ResponseEntity<Void> resp = rest.postForEntity(taskProperties.buildDTaskGoUrl("/tasks/update"), tv, Void.class);
        AssertUtils.isTrue(resp.getStatusCode().is2xxSuccessful(), "Failed to update lastRunInfo");
    }

    @Override
    public boolean exists(int taskId) {
        RestTemplate rest = new RestTemplate();
        final ResponseEntity<Boolean> resp = rest.getForEntity(taskProperties.buildDTaskGoUrl("/tasks/valid"), Boolean.class);
        AssertUtils.isTrue(resp.getStatusCode().is2xxSuccessful(), "Failed to validate task");
        final Boolean b = resp.getBody();
        return b != null && b;
    }

    @Override
    public void markTaskDisabled(int taskId, String lastRunResult, String updateBy) {
        final DisableTaskReqVo r = new DisableTaskReqVo();
        r.setId(taskId);
        r.setLastRunResult(lastRunResult);
        r.setUpdateBy(updateBy);
        r.setUpdateDate(new Date());

        RestTemplate rest = new RestTemplate();
        final ResponseEntity<Void> resp = rest.postForEntity(taskProperties.buildDTaskGoUrl("/tasks/disable"), r, Void.class);
        AssertUtils.isTrue(resp.getStatusCode().is2xxSuccessful(), "Failed to disable task");
    }
}
