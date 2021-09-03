package com.curtisnewbie.module.task.scheduling;

import com.curtisnewbie.module.task.dao.TaskEntity;
import com.curtisnewbie.module.task.vo.TaskVo;
import org.quartz.CronExpression;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * Util for {@link org.quartz.Job} , {@link JobDetail}, and {@link JobKey}
 *
 * @author yongjie.zhuang
 * @see TaskJobDetailWrapper
 */
public final class JobUtils {

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
        jobDetail.getJobDataMap().put(TaskJobDetailWrapper.JOB_DATA_MAP_IS_TRIGGERED, "true");
    }

    /**
     * Set job is triggered
     *
     * @param jobDetail job detail
     */
    @Deprecated
    public static void setIsTriggered(JobDetail jobDetail) {
        jobDetail.getJobDataMap().put(TaskJobDetailWrapper.JOB_DATA_MAP_IS_TRIGGERED, "true");
    }

    /**
     * Set job is triggered
     *
     * @param jobDetail job detail
     */
    @Deprecated
    public static boolean isJobTriggered(JobDetail jobDetail) {
        Object o = jobDetail.getJobDataMap().get(TaskJobDetailWrapper.JOB_DATA_MAP_IS_TRIGGERED);
        if (o == null)
            return false;
        return Boolean.valueOf(o.toString());
    }

    /**
     * Get run by
     *
     * @param jobDetail job detail
     * @return runBy
     */
    public static String getRunBy(JobDetail jobDetail) {
        return jobDetail.getJobDataMap().getString(TaskJobDetailWrapper.JOB_DATA_MAP_RUN_BY);
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
        return new JobKey(te.getId() + "-" + te.getJobName(), te.getAppGroup());
    }

    /**
     * Get task.id from {@link JobKey}
     */
    public static int getIdFromJobKey(JobKey jobKey) {
        String name = jobKey.getName();
        return Integer.parseInt(name.split("-")[0]);
    }

    /**
     * Get task.name from {@link JobKey}
     */
    public static String getNameFromJobKey(JobKey jobKey) {
        String name = jobKey.getName();
        String[] sp = name.split("-");
        return String.join("-", Arrays.copyOfRange(sp, 1, sp.length));
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
     * Create a temporary version of the given job
     * <br><br>
     * It's by modifying 'group' in {@link JobKey}, so it doesn't contradict to the original job, while at the same time
     * preserve the ability to retrieve it's id by {@link #getIdFromJobKey(JobKey)}
     */
    public static JobDetail createTempJob(JobDetail oldJobDetail) {

        if (isTempJob(oldJobDetail))
            throw new IllegalArgumentException("This job is already a temporary job, can't create tempJob from it");

        TaskVo taskVo = getTask(oldJobDetail);
        taskVo.setAppGroup("temporary");
        TaskJobDetailWrapper ntjw = new TaskJobDetailWrapper(taskVo);
        setIsTempJob(ntjw);
        return ntjw;
    }

    /**
     * Check if the job is a temporary job
     */
    public static boolean isTempJob(JobDetail jobDetail) {
        Object b = jobDetail.getJobDataMap().get(TaskJobDetailWrapper.JOB_DATA_MAP_IS_TEMPORARY);
        return b != null && Boolean.valueOf(b.toString());
    }

    /**
     * Mark the job as a temporary job
     */
    public static void setIsTempJob(JobDetail jobDetail) {
        jobDetail.getJobDataMap().put(TaskJobDetailWrapper.JOB_DATA_MAP_IS_TEMPORARY, "true");
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
