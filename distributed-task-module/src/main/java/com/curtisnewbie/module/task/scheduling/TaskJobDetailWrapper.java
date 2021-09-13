package com.curtisnewbie.module.task.scheduling;

import com.curtisnewbie.common.util.EnumUtils;
import com.curtisnewbie.module.task.constants.TaskConcurrentEnabled;
import com.curtisnewbie.module.task.vo.TaskVo;
import org.quartz.*;

import java.util.Objects;

/**
 * Wrapper of {@link com.curtisnewbie.module.task.dao.TaskEntity} as {@link JobDetail}
 *
 * @author yongjie.zhuang
 */
public class TaskJobDetailWrapper implements JobDetail {

    /** Key to retrieve {@link com.curtisnewbie.module.task.dao.TaskEntity} from jobDataMap */
    public static final String JOB_DATA_MAP_TASK_ENTITY = "taskEntity";

    /** Key to retrieve runBy from jobDataMap */
    public static final String JOB_DATA_MAP_RUN_BY = "runBy";

    /** Key in jobDataMap to check if a job is a temporary job */
    public static final String JOB_DATA_MAP_IS_TEMPORARY = "isTemp";

    private final TaskVo tv;
    private final JobKey jobKey;
    private final String desc;
    private final JobDataMap jobDataMap = new JobDataMap();
    private final boolean concurrentEnabled;
    private final Class<? extends Job> jobClz;

    public TaskJobDetailWrapper(TaskVo t) {
        this.tv = t;
        this.jobClz = Job.class;
        this.jobKey = JobUtils.getJobKey(t);
        this.desc = t.getJobName();
        TaskConcurrentEnabled tce = EnumUtils.parse(t.getConcurrentEnabled(), TaskConcurrentEnabled.class);
        Objects.requireNonNull(tce, "task's field 'concurrent_enabled' value illegal, unable to parse it");
        this.concurrentEnabled = tce.equals(TaskConcurrentEnabled.ENABLED);
        JobUtils.setTask(this, t);
    }

    public TaskJobDetailWrapper(TaskJobDetailWrapper w) {
        this.tv = w.tv;
        this.jobClz = w.jobClz;
        this.jobKey = w.jobKey;
        this.desc = w.desc;
        this.concurrentEnabled = w.concurrentEnabled;
        this.jobDataMap.putAll(w.jobDataMap);
    }

    @Override
    public JobKey getKey() {
        return this.jobKey;
    }

    @Override
    public String getDescription() {
        return this.desc;
    }

    @Override
    public Class<? extends Job> getJobClass() {
        return jobClz;
    }

    @Override
    public JobDataMap getJobDataMap() {
        return jobDataMap;
    }

    @Override
    public boolean isDurable() {
        return true;
    }

    @Override
    public boolean isPersistJobDataAfterExecution() {
        return false;
    }

    @Override
    public boolean isConcurrentExectionDisallowed() {
        return !concurrentEnabled;
    }

    @Override
    public boolean requestsRecovery() {
        return false;
    }

    @Override
    public Object clone() {
        return new TaskJobDetailWrapper(this);
    }

    @Override
    public JobBuilder getJobBuilder() {
        throw new UnsupportedOperationException();
    }
}
