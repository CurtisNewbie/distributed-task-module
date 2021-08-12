package com.curtisnewbie.module.task.service;

import com.curtisnewbie.common.util.EnumUtils;
import com.curtisnewbie.module.task.constants.TaskConcurrentEnabled;
import com.curtisnewbie.module.task.scheduling.TaskJobDetailWrapper;
import com.curtisnewbie.module.task.vo.TaskVo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

/**
 * @author yongjie.zhuang
 */
@Slf4j
@Service
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
    public boolean removeJob(JobKey job) throws SchedulerException {
        return scheduler().deleteJob(job);
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
        return scheduleJob(new TaskJobDetailWrapper(tv), createTrigger(tv));
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
        factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
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
