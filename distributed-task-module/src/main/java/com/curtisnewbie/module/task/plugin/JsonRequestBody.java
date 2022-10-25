package com.curtisnewbie.module.task.plugin;

import com.curtisnewbie.common.util.JsonUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @author yongj.zhuang
 */
public final class JsonRequestBody {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private JsonRequestBody() {
    }

    public static RequestBody build(Object payload) {
        return RequestBody.create(JsonUtils.uwritePretty(payload), JSON);
    }
}

