package com.curtisnewbie.module.task.constants;

import com.curtisnewbie.common.enums.IntEnum;

/**
 * task's concurrent_enabled
 *
 * @author yongjie.zhuang
 */
public enum TaskConcurrentEnabled implements IntEnum {

    /**
     * 0-disabled
     */
    DISABLED(0),

    /**
     * 1-enabled
     */
    ENABLED(1);

    private final int v;

    TaskConcurrentEnabled(int v) {
        this.v = v;
    }

    @Override
    public int getValue() {
        return v;
    }
}
