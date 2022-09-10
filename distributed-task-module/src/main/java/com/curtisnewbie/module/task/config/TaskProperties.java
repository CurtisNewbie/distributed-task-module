package com.curtisnewbie.module.task.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Properties used in this module
 *
 * @author yongjie.zhuang
 */
@Data
@Configuration
public class TaskProperties {

    public static final String DTASKGO_BASE_URL_PROP_KEY = "distributed-task-module.plugin.dtask-go.base-url";

    public static final String REFRESH_INTERVAL_PROP_KEY = "distributed-task-module.refresh-interval";
    public static final String DEFAULT_REFRESH_INTERVAL = "30";

    public static final String APP_GROUP_PROP_KEY = "distributed-task-module.application-group";
    public static final String DEFAULT_APP_GROUP = "default";

    /**
     * App Group
     */
    @Value("${" + APP_GROUP_PROP_KEY + ":" + DEFAULT_APP_GROUP + "}")
    private String appGroup;

    /**
     * Refresh interval for jobs in seconds
     */
    @Value("${" + REFRESH_INTERVAL_PROP_KEY + ":" + DEFAULT_REFRESH_INTERVAL + "}")
    private int refreshInterval;

    /**
     * dtask-go base url
     * <p>
     * http://localhost:8082/dtask/remote/
     */
    @Value("${" + DTASKGO_BASE_URL_PROP_KEY + ":}")
    private String dtaskGoBaseUrl;

    public String buildDTaskGoUrl(String relUrl) {
        return (dtaskGoBaseUrl.endsWith("/") ? dtaskGoBaseUrl : dtaskGoBaseUrl + "/") + relUrl;
    }
}
