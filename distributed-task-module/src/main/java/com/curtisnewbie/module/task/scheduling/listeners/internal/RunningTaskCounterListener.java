package com.curtisnewbie.module.task.scheduling.listeners.internal;

import com.curtisnewbie.module.task.scheduling.JobDelegate;
import com.curtisnewbie.module.task.scheduling.RunningTaskCounter;
import com.curtisnewbie.module.task.scheduling.listeners.JobPostExecuteListener;
import com.curtisnewbie.module.task.scheduling.listeners.JobPreExecuteListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Listener used to count running task
 *
 * @author yongjie.zhuang
 */
@Slf4j
public class RunningTaskCounterListener implements JobPreExecuteListener, JobPostExecuteListener, RunningTaskCounter {

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public void postExecute(JobDelegate.DelegatedJobContext context) {
        int c = atomicInteger.decrementAndGet();
        log.info("{} tasks running...", c);
    }

    @Override
    public void preExecute(JobDelegate.DelegatedJobContext context) {
        int c = atomicInteger.incrementAndGet();
        log.info("{} tasks running...", c);
    }

    @Override
    public int getCount() {
        return atomicInteger.get();
    }
}
