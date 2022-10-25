package com.curtisnewbie.module.task.plugin;

import com.curtisnewbie.common.util.*;
import com.curtisnewbie.common.vo.*;
import com.curtisnewbie.module.task.config.*;
import com.curtisnewbie.module.task.helper.*;
import com.curtisnewbie.module.task.vo.*;
import com.fasterxml.jackson.core.type.*;
import lombok.extern.slf4j.*;
import okhttp3.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static com.curtisnewbie.common.util.ExceptionUtils.illegalState;

/**
 * TaskHelper for dtask-go
 *
 * @author yongj.zhuang
 */
@Slf4j
public class DTaskGoTaskHelper implements TaskHelper {

    private final OkHttpClient client = new OkHttpClient();
    private final TaskProperties taskProperties;

    public DTaskGoTaskHelper(TaskProperties taskProperties) {
        this.taskProperties = taskProperties;
    }

    @Override
    public List<TaskVo> fetchAllTasks(String appGroup) {
        final Request request = new Request.Builder()
                .url(taskProperties.buildDTaskGoUrl("/task/all?appGroup=" + appGroup))
                .get()
                .build();
        try (Response resp = client.newCall(request).execute()) {
            Result<List<TaskVo>> result = JsonUtils.ureadValueAsObject(resp.body().string(), new TypeReference<Result<List<TaskVo>>>() {
            });
            result.assertIsOk();
            return result.getData() != null ? result.getData() : new ArrayList<>();
        } catch (IOException e) {
            throw illegalState(e, "Failed to fetchAllTasks, appGroup: %s", appGroup);
        }
    }

    @Override
    public void updateLastRunInfo(UpdateLastRunInfoReq tv) {
        final Request request = new Request.Builder()
                .url(taskProperties.buildDTaskGoUrl("/task/lastRunInfo/update"))
                .post(JsonRequestBody.build(tv))
                .build();
        try (Response resp = client.newCall(request).execute()) {
            final Result<?> result = JsonUtils.ureadValueAsObject(resp.body().string(), Result.class);
            result.assertIsOk();
        } catch (IOException e) {
            throw illegalState(e, "Failed to updateLastRunInfo, req: %s", tv);
        }
    }

    @Override
    public boolean isEnabled(int taskId) {
        final Request request = new Request.Builder()
                .url(taskProperties.buildDTaskGoUrl("/task/valid?taskId=" + taskId))
                .get()
                .build();
        try (Response resp = client.newCall(request).execute();) {
            final Result<?> result = JsonUtils.ureadValueAsObject(resp.body().string(), Result.class);
            return result.isOk();
        } catch (IOException e) {
            throw illegalState(e, "Failed to check task's isEnabled, taskId: %s", taskId);
        }
    }

    @Override
    public void markTaskDisabled(int taskId, String lastRunResult, String updateBy) {
        final DisableTaskReqVo r = new DisableTaskReqVo();
        r.setId(taskId);
        r.setLastRunResult(lastRunResult);
        r.setUpdateBy(updateBy);
        r.setUpdateDate(LocalDateTime.now());

        final Request request = new Request.Builder()
                .url(taskProperties.buildDTaskGoUrl("/task/disable"))
                .post(JsonRequestBody.build(r))
                .build();

        try (Response resp = client.newCall(request).execute();) {
            final Result<?> result = JsonUtils.ureadValueAsObject(resp.body().string(), Result.class);
            result.assertIsOk();
        } catch (IOException e) {
            throw illegalState(e, "Failed to markTaskDisabled, taskId: %s, lastRunResult: %s, updateBy: %s", taskId, lastRunResult, updateBy);
        }
    }

    @Override
    public void declareTask(DeclareTaskReq req) {
        final Request request = new Request.Builder()
                .url(taskProperties.buildDTaskGoUrl("/task/declare"))
                .post(JsonRequestBody.build(req))
                .build();

        try (Response resp = client.newCall(request).execute();) {
            final Result<?> result = JsonUtils.ureadValueAsObject(resp.body().string(), Result.class);
            result.assertIsOk();
            log.info("DeclareTask success, resp: {}", result);
        } catch (IOException e) {
            throw illegalState(e, "Failed to declareTask, req: %s", req);
        }

    }
}
