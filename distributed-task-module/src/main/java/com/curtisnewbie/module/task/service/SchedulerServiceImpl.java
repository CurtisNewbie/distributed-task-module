package com.curtisnewbie.module.task.service;

import com.curtisnewbie.common.util.EnumUtils;
import com.curtisnewbie.module.task.constants.TaskConcurrentEnabled;
import com.curtisnewbie.module.task.scheduling.JobUtils;
import com.curtisnewbie.module.task.scheduling.TaskJobDetailWrapper;
import com.curtisnewbie.module.task.vo.TaskVo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

/**
 * @author yongjie.zhuang
 */
@Slf4j
public class SchedulerServiceImpl implements SchedulerService {

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Override
    public void triggerJob(JobKey jobKey) throws SchedulerException {
        Scheduler scheduler = scheduler();
        if (!scheduler.checkExists(jobKey)) {
            throw new SchedulerException("Job " + jobKey.getName() + " doesn't exist, can't be scheduled");
        }
        scheduler.triggerJob(jobKey);
    }

    @Override
    public void createRunOnceTrigger(@NotNull JobKey jobKey, @NotEmpty String triggeredBy) throws SchedulerException {
        Scheduler scheduler = scheduler();
        if (!scheduler.checkExists(jobKey)) {
            throw new SchedulerException("Job " + jobKey.getName() + " doesn't exist, can't be scheduled");
        }
        // without any schedule configured, the trigger will only run once
        Trigger runOnceTrigger = TriggerBuilder.newTrigger()
                .startNow()
                .forJob(jobKey)
                .usingJobData(JobUtils.RUN_ONCE_TRIGGER, Boolean.TRUE.toString())
                .usingJobData(TaskJobDetailWrapper.JOB_DATA_MAP_RUN_BY, triggeredBy)
                .build();
        scheduler.scheduleJob(runOnceTrigger);
    }

    @Override
    public void removeTrigger(TriggerKey triggerKey) throws SchedulerException {
        scheduler().unscheduleJob(triggerKey);
    }

    @Override
    public boolean removeJob(JobKey job) throws SchedulerException {
        return scheduler().deleteJob(job);
    }

    @Override
    public void addUnscheduledJob(JobDetail jobDetail, boolean replace) throws SchedulerException {
        scheduler().addJob(jobDetail, replace);
    }

    @Override
    public Date scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
        return scheduler().scheduleJob(jobDetail, trigger);
    }

    @Override
    public Date scheduleJob(TaskVo tv) throws SchedulerException, ParseException {
        log.info("Scheduling task: id: '{}', name: '{}' cron_expr: '{}', target_bean: '{}', concurrent_enabled: '{}'",
                tv.getId(), tv.getJobName(),
                tv.getCronExpr(), tv.getTargetBean(),
                EnumUtils.parse(tv.getConcurrentEnabled(), TaskConcurrentEnabled.class));
        JobDetail jd = new TaskJobDetailWrapper(tv);
        return scheduleJob(jd, createTrigger(tv));
    }

    @Override
    public Optional<JobDetail> getJob(JobKey jobKey) throws SchedulerException {
        return Optional.ofNullable(scheduler().getJobDetail(jobKey));
    }

    @Override
    public Set<JobKey> getJobKeySet(GroupMatcher<JobKey> gm) throws SchedulerException {
        return scheduler().getJobKeys(gm);
    }

    @Override
    public Trigger createTrigger(TaskVo tv) throws ParseException {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setName(tv.getJobName());
        factoryBean.setStartTime(new Date());
        factoryBean.setCronExpression(tv.getCronExpr());
        // todo specify this in taskVo ?
        factoryBean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    @Override
    public void removeAllJobs() throws SchedulerException {
        scheduler().clear();
    }

    private Scheduler scheduler() {
        return this.schedulerFactoryBean.getScheduler();
    }

}
