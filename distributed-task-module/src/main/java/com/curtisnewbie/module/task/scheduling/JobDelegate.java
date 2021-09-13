package com.curtisnewbie.module.task.scheduling;

import com.curtisnewbie.common.util.AppContextHolder;
import com.curtisnewbie.module.redisutil.RedisController;
import com.curtisnewbie.module.task.scheduling.listeners.JobPostExecuteListener;
import com.curtisnewbie.module.task.scheduling.listeners.JobPreExecuteListener;
import com.curtisnewbie.module.task.vo.TaskVo;
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
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        final JobKey jk = context.getJobDetail().getKey();
        if (context.getJobDetail().isConcurrentExectionDisallowed()) {
            try {
                acquireMutexLock(jk);
            } catch (InterruptedException e) {
                throw new JobExecutionException(e);
            }
        }

        doPreExecute();

        TaskVo task = JobUtils.getTask(context.getJobDetail());
        log.info("Execute job: id: '{}', name: '{}'", task.getId(), task.getJobName());
        try {
            ctx.startTime = new Date();

            // execute delegated job
            this.ctx.job.execute(context);

            ctx.endTime = new Date();
        } catch (Exception e) {
            // record if any exception occurred as well the time it starts or ends
            ctx.exception = e;
        } finally {
            if (isLocked)
                releaseMutexLock(jk);
        }

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

        // loop until it gets the lock
        while (!redisController.tryLock(getConcurrentLockKey(key)))
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
        for (JobPreExecuteListener jl : jobPreExecuteListenerList)
            jl.preExecute(ctx.copy());
    }

    private void doPostExecute() {
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
            copy.job = job;
            copy.jobExecutionContext = jobExecutionContext;
            copy.startTime = startTime;
            copy.endTime = endTime;
            copy.exception = exception;
            return copy;
        }
    }
}
