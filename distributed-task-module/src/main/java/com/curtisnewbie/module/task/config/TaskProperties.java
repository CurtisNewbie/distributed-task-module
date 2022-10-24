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

    public static final String APP_GROUP_KEY = "distributed-task-module.application-group";
    public static final String DEFAULT_APP_GROUP = "default";

    /**
     * App Group
     */
    @Value("${" + APP_GROUP_KEY + ":" + DEFAULT_APP_GROUP + "}")
    private String appGroup;

    /**
     * Refresh interval for jobs in seconds
     */
    @Value("${distributed-task-module.refresh-interval:30}")
    private int refreshInterval;

    /**
     * dtask-go base url
     * <p>
     * http://localhost:8082/dtask/remote/
     */
    @Value("${distributed-task-module.plugin.dtask-go.base-url:}")
    private String dtaskGoBaseUrl;

    /**
     * Whether scheduling is disabled, by default it's false
     */
    @Value("${distributed-task-module.scheduling.disabled:false}")
    private boolean schedulingDisabled;

    /**
     * Whether dtask-go plugin is enabled, by default it's false
     */
    @Value("${distributed-task-module.plugin.dtask-go.enabled:false}")
    private boolean dTaskGoPluginEnabled;

    public String buildDTaskGoUrl(String relUrl) {
        return (dtaskGoBaseUrl.endsWith("/") ? dtaskGoBaseUrl : dtaskGoBaseUrl + "/") + relUrl;
    }
}
