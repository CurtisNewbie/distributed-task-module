package com.curtisnewbie.module.task.scheduling;

import lombok.Data;
import org.quartz.JobKey;

/**
 * JobKey for manually triggered jobs
 *
 * @author yongjie.zhuang
 */
@Data
public class TriggeredJobKey {

    private String name;

    private String group;

    private String triggerBy;

    public JobKey toJobKey() {
        return new JobKey(name, group);
    }

    public static TriggeredJobKey fromJobKey(JobKey jobKey) {
        TriggeredJobKey sjk = new TriggeredJobKey();
        sjk.setName(jobKey.getName());
        sjk.setGroup(jobKey.getGroup());
        return sjk;
    }
}
