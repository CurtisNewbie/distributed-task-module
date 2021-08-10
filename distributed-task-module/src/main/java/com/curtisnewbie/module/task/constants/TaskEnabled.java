package com.curtisnewbie.module.task.constants;

import com.curtisnewbie.common.enums.IntEnum;

/**
 * task's enabled
 *
 * @author yongjie.zhuang
 */
public enum TaskEnabled implements IntEnum {

    /** 0-disabled */
    DISABLED(0),

    /** 1-enabled */
    ENABLED(1);

    private final int v;

    TaskEnabled(int v) {
        this.v = v;
    }

    @Override
    public int getValue() {
        return v;
    }
}
