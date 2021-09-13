package com.curtisnewbie.module.task.scheduling;

import com.curtisnewbie.module.task.dao.TaskEntity;
import com.curtisnewbie.module.task.vo.TaskVo;
import org.quartz.*;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Util for {@link org.quartz.Job} , {@link JobDetail}, and {@link JobKey}
 *
 * @author yongjie.zhuang
 * @see TaskJobDetailWrapper
 */
public final class JobUtils {

    /** Key to check if job is fired by a 'run-once' Trigger */
    public static final String RUN_ONCE_TRIGGER = "run-once-trigger";

    private JobUtils() {

    }

    /**
     * Set run by
     *
     * @param jobDetail job detail
     * @param runBy     runBy
     */
    public static void setRunBy(JobDetail jobDetail, String runBy) {
        jobDetail.getJobDataMap().put(TaskJobDetailWrapper.JOB_DATA_MAP_RUN_BY, runBy);
    }

    /**
     * Mark the job as being fired by a 'run-once' Trigger
     */
    public static void setIsRunOnceTrigger(JobDataMap m) {
        m.put(RUN_ONCE_TRIGGER, "true");
    }

    /**
     * Check if the job is fired by a 'run-once' Trigger
     */
    public static boolean isRunOnceTrigger(JobDataMap m) {
        Object o = m.get(RUN_ONCE_TRIGGER);
        if (o == null)
            return false;
        return Boolean.valueOf(o.toString());
    }

    /**
     * Get run by
     *
     * @param mergedJobDataMap merged JobDataMap
     * @return runBy
     */
    public static String getRunBy(JobDataMap mergedJobDataMap) {
        return mergedJobDataMap.getString(TaskJobDetailWrapper.JOB_DATA_MAP_RUN_BY);
    }

    /**
     * Check if cron expression is valid
     */
    public static boolean isCronExprValid(String cronExpr) {
        if (!StringUtils.hasText(cronExpr))
            return false;
        return CronExpression.isValidExpression(cronExpr);
    }

    /**
     * Get {@link JobKey} from {@link TaskVo}
     */
    public static JobKey getJobKey(TaskVo te) {
        Objects.requireNonNull(te.getId());
        return new JobKey(String.valueOf(te.getId()), te.getAppGroup());
    }

    /**
     * Get task.id from {@link JobKey}
     */
    public static int getIdFromJobKey(JobKey jobKey) {
        String name = jobKey.getName();
        Objects.requireNonNull(name);
        return Integer.parseInt(name);
    }

    /** See if the job detail has changed */
    public static boolean isJobDetailChanged(JobDetail oldJd, TaskVo tv) {
        TaskVo oldTe = getTask(oldJd);
        if (!Objects.equals(oldTe.getEnabled(), tv.getEnabled()))
            return true;
        if (!Objects.equals(oldTe.getJobName(), tv.getJobName()))
            return true;
        if (!Objects.equals(oldTe.getAppGroup(), tv.getAppGroup()))
            return true;
        if (!Objects.equals(oldTe.getConcurrentEnabled(), tv.getConcurrentEnabled()))
            return true;
        if (!Objects.equals(oldTe.getCronExpr(), tv.getCronExpr()))
            return true;
        return false;
    }

    /**
     * Get {@link TaskEntity} from JobDetail's jobDataMap
     */
    public static TaskVo getTask(JobDetail jobDetail) {
        return (TaskVo) jobDetail.getJobDataMap().get(TaskJobDetailWrapper.JOB_DATA_MAP_TASK_ENTITY);
    }

    /**
     * Set {@link TaskEntity} to JobDetail's jobDataMap
     */
    public static void setTask(JobDetail jd, TaskVo taskVo) {
        jd.getJobDataMap().put(TaskJobDetailWrapper.JOB_DATA_MAP_TASK_ENTITY, taskVo);
    }

    /**
     * Convert result string
     */
    public static String convertResult(JobDelegate.DelegatedJobContext ctx) {
        return ctx.getException() != null ?
                "exception " + ctx.getException().getClass().getSimpleName() + " occurred"
                : "success";
    }
}
