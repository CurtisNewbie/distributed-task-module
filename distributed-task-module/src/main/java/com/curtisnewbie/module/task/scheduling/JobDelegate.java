package com.curtisnewbie.module.task.scheduling;

import com.curtisnewbie.common.util.*;
import com.curtisnewbie.module.redisutil.RedisController;
import com.curtisnewbie.module.task.scheduling.listeners.JobPostExecuteListener;
import com.curtisnewbie.module.task.scheduling.listeners.JobPreExecuteListener;
import com.curtisnewbie.module.task.vo.TaskVo;
import jodd.bean.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.context.ApplicationContext;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Delegate of a job
 *
 * @author yongjie.zhuang
 */
@Slf4j
public class JobDelegate implements Job, ListenableJob {

    private final List<JobPostExecuteListener> jobPostExecuteListenerList = new LinkedList<>();
    private final List<JobPreExecuteListener> jobPreExecuteListenerList = new LinkedList<>();
    private final DelegatedJobContext ctx = new DelegatedJobContext();
    private boolean isLocked = false;

    public JobDelegate(Job job, JobDetail jobDetail) {
        log.debug("Creating delegate for job '{}'", jobDetail.getKey().getName());
        ctx.job = job;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        ctx.jobExecutionContext = context;
        final JobKey jk = context.getJobDetail().getKey();

        try {
            if (context.getJobDetail().isConcurrentExectionDisallowed()) {
                try {
                    acquireMutexLock(jk);
                } catch (InterruptedException e) {
                    throw new JobExecutionException(e);
                }
            }

            // pre-execute lifecycle callbacks
            doPreExecute();

            ctx.task = JobUtils.getTask(context.getJobDetail());
            log.info("About to execute job: id: '{}', name: '{}'", ctx.task.getId(), ctx.task.getJobName());

            // execute delegated job
            ctx.startTime = new Date();
            try {
                this.ctx.job.execute(context);
            } catch (Exception e) {
                log.error("Job '{}' throws exception", ctx.task.getJobName(), e);
                ctx.exception = e; // this will be handled by postExecute lifecycle callbacks
            }
            ctx.endTime = new Date();

            // post-execute lifecycle callbacks
            doPostExecute();
        } finally {
            releaseMutexLock(jk);
        }
    }

    private void releaseMutexLock(JobKey key) {
        if (!isLocked) return;
        ApplicationContext applicationContext = AppContextHolder.getApplicationContext();
        Objects.requireNonNull(applicationContext, ApplicationContext.class.getSimpleName() + " not found");
        RedisController redisController = applicationContext.getBean(RedisController.class);
        Objects.requireNonNull(redisController);

        redisController.unlock(getConcurrentLockKey(key));
    }

    private void acquireMutexLock(JobKey key) throws InterruptedException {
        ApplicationContext applicationContext = AppContextHolder.getApplicationContext();
        Objects.requireNonNull(applicationContext, ApplicationContext.class.getSimpleName() + " not found");
        RedisController redisController = applicationContext.getBean(RedisController.class);
        Objects.requireNonNull(redisController);

        // loop until it gets the lock
        while (!(isLocked = redisController.tryLock(getConcurrentLockKey(key))))
            ;
    }

    @Override
    public void onPreExecute(JobPreExecuteListener l) {
        Objects.requireNonNull(l, JobPreExecuteListener.class.getSimpleName() + " can't be null");
        jobPreExecuteListenerList.add(l);
    }

    @Override
    public void onPostExecute(JobPostExecuteListener l) {
        Objects.requireNonNull(l, JobPostExecuteListener.class.getSimpleName() + " can't be null");
        jobPostExecuteListenerList.add(l);
    }

    private String getConcurrentLockKey(JobKey key) {
        return "task:exec:concurrent:" + key.getGroup() + ":" + key.getName();
    }


    private void doPreExecute() {
        for (JobPreExecuteListener jl : jobPreExecuteListenerList)
            jl.preExecute(ctx.copy());
    }

    private void doPostExecute() {
        for (JobPostExecuteListener jl : jobPostExecuteListenerList) {
            try {
                jl.postExecute(ctx.copy());
            } catch (Exception e) {
                log.error("Exception thrown while trying to call #postExecute", e);
            }
        }
    }

    /**
     * Execution context of delegated job
     */
    @Data
    @NoArgsConstructor
    public static class DelegatedJobContext {

        /** Task */
        private TaskVo task;

        /** actual job that is executed */
        private Job job;

        /** Execution context of the job */
        private JobExecutionContext jobExecutionContext;

        /** when the job starts */
        private Date startTime;

        /** when the job ends */
        private Date endTime;

        /**
         * exception that may have occurred during the job execution, if not exception was thrown, it will be null
         */
        private Exception exception;

        private DelegatedJobContext copy() {
            DelegatedJobContext copy = new DelegatedJobContext();
            copy.task = BeanCopyUtils.toType(task, TaskVo.class); // deep copy
            copy.job = job;
            copy.jobExecutionContext = jobExecutionContext;
            copy.startTime = startTime;
            copy.endTime = endTime;
            copy.exception = exception;
            return copy;
        }
    }
}
