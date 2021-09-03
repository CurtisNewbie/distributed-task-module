package com.curtisnewbie.module.task.scheduling;

import com.curtisnewbie.common.util.AppContextHolder;
import com.curtisnewbie.module.redisutil.RedisController;
import com.curtisnewbie.module.task.scheduling.listeners.JobPostExecuteListener;
import com.curtisnewbie.module.task.scheduling.listeners.JobPreExecuteListener;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.context.ApplicationContext;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Delegate of a job
 *
 * @author yongjie.zhuang
 */
@Slf4j
public class JobDelegate implements Job, ListenableJob {

    private final List<JobPostExecuteListener> jobPostExecuteListenerList = new LinkedList<>();
    private final List<JobPreExecuteListener> jobPreExecuteListenerList = new LinkedList<>();
    private DelegatedJobContext ctx = new DelegatedJobContext();
    private boolean isLocked = false;

    public JobDelegate(Job job, JobDetail jobDetail) {
        log.debug("Creating delegate for job '{}'", jobDetail.getKey().getName());
        ctx.job = job;
        ctx.jobDetail = jobDetail;
    }
@Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        if (ctx.getJobDetail().isConcurrentExectionDisallowed()) {
            try {
                acquireMutexLock(ctx.getJobDetail().getKey());
            } catch (InterruptedException e) {
                throw new JobExecutionException(e);
            }
        }

        doPreExecute();

        log.info("Execute job: '{}'", ctx.jobDetail.getKey().getName());
        ctx.startTime = new Date();
        try {
            // execute delegated job
            this.ctx.job.execute(context);
        } catch (Exception e) {
            // record if any exception occurred as well the time it starts or ends
            ctx.exception = e;
        }
        ctx.endTime = new Date();

        if (isLocked)
            releaseMutexLock(ctx.getJobDetail().getKey());

        doPostExecute();
    }

    private void releaseMutexLock(JobKey key) {
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

        // looping until it gets the lock, the lock is hold for an hour to make sure the task is executed exclusively
        while (!redisController.tryLock(getConcurrentLockKey(key), 0, 60, TimeUnit.HOURS))
            ;
        isLocked = true;
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
        log.debug("Invoking {} registered {} on '{}'",
                jobPreExecuteListenerList.size(),
                JobPreExecuteListener.class.getSimpleName(),
                ctx.jobDetail.getKey().getName());
        for (JobPreExecuteListener jl : jobPreExecuteListenerList)
            jl.preExecute(ctx.copy());
    }

    private void doPostExecute() {
        log.debug("Invoking {} registered {} on '{}'",
                jobPostExecuteListenerList.size(),
                JobPostExecuteListener.class.getSimpleName(),
                ctx.jobDetail.getKey().getName());
        for (JobPostExecuteListener jl : jobPostExecuteListenerList)
            jl.postExecute(ctx.copy());
    }

    /**
     * Execution context of delegated job
     */
    @Data
    @NoArgsConstructor
    public static class DelegatedJobContext {

        /** actual job that is executed */
        private Job job;

        /** job' detail */
        private JobDetail jobDetail;

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
            copy.job = job;
            copy.jobDetail = jobDetail;
            copy.startTime = startTime;
            copy.endTime = endTime;
            copy.exception = exception;
            return copy;
        }
    }
}
