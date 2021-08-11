package com.curtisnewbie.module.task.scheduling.listeners.internal;

import com.curtisnewbie.module.task.scheduling.JobDelegate;
import com.curtisnewbie.module.task.scheduling.listeners.JobPostExecuteListener;
import com.curtisnewbie.module.task.scheduling.listeners.JobPreExecuteListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Listener used to count running task
 *
 * @author yongjie.zhuang
 */
@Slf4j
@Component
public class TaskRunningCounter implements JobPreExecuteListener, JobPostExecuteListener {

    private AtomicLong atomicLong = new AtomicLong(0);

    @Override
    public void postExecute(JobDelegate.JobExecContext context) {
        long c = atomicLong.decrementAndGet();
        log.info("{} tasks running...", c);
    }

    @Override
    public void preExecute(JobDelegate.JobExecContext context) {
        long c = atomicLong.incrementAndGet();
        log.info("{} tasks running...", c);
    }
}
