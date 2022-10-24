package com.curtisnewbie.module.task.annotation;

import com.curtisnewbie.common.util.ReflectUtils;
import com.curtisnewbie.module.task.config.TaskProperties;
import com.curtisnewbie.module.task.constants.TaskConcurrentEnabled;
import com.curtisnewbie.module.task.constants.TaskEnabled;
import com.curtisnewbie.module.task.helper.TaskHelper;
import com.curtisnewbie.module.task.vo.DeclareTaskReq;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;

/**
 * Reporter of Job declaration
 *
 * @author yongj.zhuang
 */
@Slf4j
public class JobDeclarationReporter implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        final TaskHelper taskHelper = applicationContext.getBean(TaskHelper.class);
        final String appGroup = applicationContext.getEnvironment().getProperty(TaskProperties.APP_GROUP_KEY, TaskProperties.DEFAULT_APP_GROUP);
        final Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(JobDeclaration.class);

        // collect first
        final List<DeclareTaskReq> declarations = beanMap.entrySet().stream().map(kv -> {
                    String beanName = kv.getKey();
                    Object bean = kv.getValue();
                    if (!(bean instanceof Job)) {
                        log.warn("Bean '{}' is not instance of org.quartz.Job, @JobDeclaration shouldn't be used", beanName);
                        return null;
                    }

                    final JobDeclaration jd = ReflectUtils.annotationOnClass(bean.getClass(), JobDeclaration.class).orElse(null);
                    if (jd == null) return null; // not gonna happen, but we keep it here

                    DeclareTaskReq req = new DeclareTaskReq();
                    req.setJobName(hasText(jd.name()) ? jd.name() : beanName);
                    req.setTargetBean(beanName);
                    req.setAppGroup(appGroup);
                    req.setCronExpr(jd.cron());
                    req.setEnabled(jd.enabled() ? TaskEnabled.ENABLED.getValue() : TaskEnabled.DISABLED.getValue());
                    req.setConcurrentEnabled(jd.concurrent() ? TaskConcurrentEnabled.ENABLED.getValue() : TaskConcurrentEnabled.DISABLED.getValue());
                    req.setOverridden(jd.overridden());
                    return req;
                })
                .filter(Objects::nonNull)
                .peek(dec -> log.info("Parsed JobDeclaration: {}", dec))
                .collect(Collectors.toList());

        // declare asynchronously, it may fail, but it shouldn't be a problem
        CompletableFuture.runAsync(() -> declarations.forEach(dec -> {
            try {
                taskHelper.declareTask(dec);
                log.info("Task declared, req: {}", dec);
            } catch (Exception e) {
                log.error("Failed to declare task, req: {}", dec, e);
            }
        }));
    }
}
