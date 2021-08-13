package com.curtisnewbie.module.task.scheduling;

import lombok.Data;
import org.quartz.JobKey;

/**
 * Serializable jobKey
 *
 * @author yongjie.zhuang
 */
@Data
public class SerializableJobKey {

    private String name;

    private String group;

    private String triggerBy;

    public JobKey toJobKey() {
        return new JobKey(name, group);
    }

    public static SerializableJobKey fromJobKey(JobKey jobKey) {
        SerializableJobKey sjk = new SerializableJobKey();
        sjk.setName(jobKey.getName());
        sjk.setGroup(jobKey.getGroup());
        return sjk;
    }
}
