package com.curtisnewbie.module.task.scheduling;

import com.curtisnewbie.module.task.dao.TaskEntity;
import com.curtisnewbie.module.task.vo.TaskVo;
import org.quartz.CronExpression;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.springframework.util.StringUtils;

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
    public static void setIsTriggered(JobDetail jobDetail) {
        jobDetail.getJobDataMap().put(TaskJobDetailWrapper.JOB_DATA_MAP_IS_TRIGGERED, "true");
    }

    /**
     * Set job is triggered
     *
     * @param jobDetail job detail
     */
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
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < sp.length; i++) {
            sb.append(sp[i]);
        }
        return sb.toString();
    }

    /** See if the job detail has changed */
    public static boolean isJobDetailChanged(JobDetail oldJd, TaskVo tv) {
        TaskJobDetailWrapper oldJdw = (TaskJobDetailWrapper) oldJd;
        TaskVo oldTe = oldJdw.getTaskVo();
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
    public static TaskVo getTaskFromJobDataMap(JobDetail jobDetail) {
        return (TaskVo) jobDetail.getJobDataMap().get(TaskJobDetailWrapper.JOB_DATA_MAP_TASK_ENTITY);
    }
}
