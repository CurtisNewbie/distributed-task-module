package com.curtisnewbie.module.task.helper;

import com.curtisnewbie.module.task.vo.*;

/**
 * Task History Helper
 * <p>
 * Helper used by the scheduling functionalities, one may implement this interface as a bean to change how task history is saved.
 *
 * @author yongj.zhuang
 */
public interface TaskHistoryHelper {

    /**
     * Save task history
     */
    void saveTaskHistory(TaskHistoryVo v);
}
