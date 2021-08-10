package com.curtisnewbie.module.task.scheduling;

import com.curtisnewbie.module.task.scheduling.listeners.JobPostExecuteListener;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Wrapper of job
 *
 * @author yongjie.zhuang
 */
@Slf4j
public class JobProxy implements Job, ListenableJob {

    private final List<JobPostExecuteListener> jobPostExecuteListenerList = new LinkedList<>();
    private JobExecContext ctx = new JobExecContext();

    public JobProxy(Job job, JobDetail jobDetail) {
        log.info("Creating job proxy for job '{}'", jobDetail.getKey().getName());
        ctx.job = job;
        ctx.jobDetail = jobDetail;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Execute proxied job: '{}'", ctx.jobDetail.getKey().getName());

        ctx.startTime = new Date();
        try {
            // execute proxied job
            this.ctx.job.execute(context);
        } catch (Exception e) {
            // record if any exception occurred as well the time it starts or ends
            ctx.exception = e;
        }
        ctx.endTime = new Date();

        // call the registered listeners
        doPostExecute();
    }

    @Override
    public void onPostExecute(JobPostExecuteListener l) {
        jobPostExecuteListenerList.add(l);
    }

    private void doPostExecute() {
        log.info("Proxied job finished, invoking registered {}: '{}'", JobPostExecuteListener.class.getSimpleName(),
                ctx.jobDetail.getKey().getName());
        for (JobPostExecuteListener jl : jobPostExecuteListenerList)
            jl.postExecute(ctx);
    }

    /**
     * Execution context of proxied job
     */
    @Data
    @NoArgsConstructor
    public static class JobExecContext {
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
    }
}
