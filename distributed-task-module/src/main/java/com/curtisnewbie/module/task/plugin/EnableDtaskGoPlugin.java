package com.curtisnewbie.module.task.plugin;

import com.curtisnewbie.common.preconf.*;
import org.springframework.context.annotation.*;

import java.lang.annotation.*;

/**
 * @author yongj.zhuang
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({DTaskGoTaskHelper.class, DTaskGoTaskHistoryHelper.class, RestTemplatePreConfigured.class})
public @interface EnableDtaskGoPlugin {
}
