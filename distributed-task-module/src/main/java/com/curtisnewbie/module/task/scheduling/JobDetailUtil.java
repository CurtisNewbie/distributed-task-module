package com.curtisnewbie.module.task.scheduling;

import com.curtisnewbie.module.task.dao.TaskEntity;
import org.quartz.JobDetail;
import org.quartz.JobKey;

import java.util.Objects;

/**
 * Util for {@link org.quartz.JobDetail}
 *
 * @author yongjie.zhuang
 * @see TaskJobDetailWrapper
 */
public final class JobDetailUtil {

    private JobDetailUtil() {

    }

    /**
     * Get {@link JobKey} from {@link TaskEntity}
     */
    public static JobKey getJobKey(TaskEntity te) {
        return new JobKey(te.getJobName(), te.getAppGroup());
    }

    /** See if the job detail has changed */
    public static boolean isJobDetailChanged(JobDetail oldJd, TaskEntity te) {
        TaskJobDetailWrapper oldJdw = (TaskJobDetailWrapper) oldJd;
        TaskEntity oldTe = oldJdw.getTaskEntity();
        if (!Objects.equals(oldTe.getEnabled(), te.getEnabled()))
            return true;
        if (!Objects.equals(oldTe.getJobName(), te.getJobName()))
            return true;
        if (!Objects.equals(oldTe.getAppGroup(), te.getAppGroup()))
            return true;
        if (!Objects.equals(oldTe.getConcurrentEnabled(), te.getConcurrentEnabled()))
            return true;
        if (!Objects.equals(oldTe.getCronExpr(), te.getCronExpr()))
            return true;
        return false;
    }

    /**
     * Get {@link TaskEntity} from JobDetail's jobDataMap
     */
    public static TaskEntity getTaskEntityFromJobDataMap(JobDetail jobDetail) {
        return (TaskEntity) jobDetail.getJobDataMap().get(TaskJobDetailWrapper.JOD_DATA_MAP_TASK_ENTITY);
    }
}
