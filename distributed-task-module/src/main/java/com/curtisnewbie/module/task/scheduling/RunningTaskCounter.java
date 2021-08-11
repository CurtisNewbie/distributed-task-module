package com.curtisnewbie.module.task.scheduling;

/**
 * Counter of tasks that are running in current application/node
 *
 * @author yongjie.zhuang
 */
public interface RunningTaskCounter {

    /**
     * Get count of tasks that are currently running
     *
     * @return count
     */
    int getCount();
}
