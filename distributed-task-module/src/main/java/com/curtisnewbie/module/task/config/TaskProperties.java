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

    public static final String APP_GROUP_PROP_KEY = "distributed-task-module.application-group";
    public static final String DEFAULT_APP_GROUP = "default";

    @Value("${" + APP_GROUP_PROP_KEY + ":" + DEFAULT_APP_GROUP + "}")
    private String appGroup;
}
