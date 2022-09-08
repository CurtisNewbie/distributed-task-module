package com.curtisnewbie.module.task.helper;

import com.curtisnewbie.module.task.helper.impl.*;
import com.curtisnewbie.module.task.vo.*;

/**
 * Task History Helper
 * <p>
 * Helper used by the scheduling functionalities, one may implement this interface as a bean to change how task history is saved.
 * <p>
 * The default implementation is {@link LocalDBTaskHistoryHelper}, which essentially reads/updates the database connected by the service,
 * and looks for a table called {@code task_history}
 *
 * @author yongj.zhuang
 */
public interface TaskHistoryHelper {

    /**
     * Save task history
     */
    void saveTaskHistory(TaskHistoryVo v);
}
