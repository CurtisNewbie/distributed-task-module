package com.curtisnewbie.module.task.plugin;

import com.curtisnewbie.common.util.*;
import com.curtisnewbie.common.vo.*;
import com.curtisnewbie.module.task.config.*;
import com.curtisnewbie.module.task.helper.*;
import com.curtisnewbie.module.task.vo.*;
import lombok.extern.slf4j.*;
import okhttp3.*;
import org.springframework.web.client.*;

import java.io.IOException;

import static com.curtisnewbie.common.util.ExceptionUtils.illegalState;


/**
 * TaskHistoryHelper for dtask-go
 *
 * @author yongj.zhuang
 */
@Slf4j
public class DTaskGoTaskHistoryHelper implements TaskHistoryHelper {

    private final OkHttpClient client = new OkHttpClient();
    private final TaskProperties taskProperties;

    public DTaskGoTaskHistoryHelper(TaskProperties taskProperties) {
        this.taskProperties = taskProperties;
    }

    @Override
    public void saveTaskHistory(TaskHistoryVo v) {
        final Request request = new Request.Builder()
                .url(taskProperties.buildDTaskGoUrl("/task/history"))
                .post(JsonRequestBody.build(v))
                .build();
        try (Response resp = client.newCall(request).execute();) {
            final Result<?> result = JsonUtils.ureadValueAsObject(resp.body().string(), Result.class);
            AssertUtils.notNull(result, "Failed to fetch tasks from dtask-go");
            result.assertIsOk();
        } catch (IOException e) {
            throw illegalState("Failed to saveTaskHistory, req: %s", v, e);
        }
    }
}
