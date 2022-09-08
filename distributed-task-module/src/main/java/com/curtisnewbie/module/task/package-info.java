/**
 * <h1>
 * Distributed Task Module
 * </h1>
 * <p>
 * To use this module, make sure you properly configure your database and redis.
 * <p>
 * First of all, you need to create your job instances by extending {@link com.curtisnewbie.module.task.scheduling.AbstractJob},
 * and making them spring managed beans.
 * <p>
 * Then you need to configure your database table <b>'{@code task}'</b>: the {@code target_bean} field is
 * the name of the job bean, and the <b>'app_group'</b> field is the name of your cluster, all services of the same cluster will for sure use the same 'app_group' value.
 * <p>
 * For example, our job is declared as a spring managed bean with bean name 'physicalDeletingFileJob' (this should be
 * same as the value in field 'target_bean').
 * <pre>
 * {@code
 * @Component
 * public class PhysicalDeletingFileJob extends AbstractJob {
 *
 *     @Override
 *     protected void executeInternal(TaskVo task) throws JobExecutionException {
 *          // write your logic here
 *     }
 * }
 * }
 * </pre>
 * <p>
 * Then in our table, we have
 * <pre>
 * {@code
 * job_name='fileDeleteJob'
 * target_bean='physicalDeletingFileJob'
 * cron_expr='0 0/2 * ? * *'
 * app_group='file-server'
 * enabled=1
 * concurrent_enabled=1
 * }
 * </pre>
 * <p>
 * Finally, in our property file, we configure the following property using the same value as the 'app_group' in database.
 * </p>
 * <pre>
 * {@code
 * # name of the application group (same cluster should share the same name)
 * distributed-task-module.application-group=file-server
 * }
 * </pre>
 * <p>
 * Note that if property "distributed-task-module.enabled" is set to false, this module will be disabled.
 * </p>
 * <p>
 * And if the property "distributed-task-module.scheduling.disabled" is set to true, current node will not participate
 * in node coordination or task execution. You can still autowire and use {@link com.curtisnewbie.module.task.service.NodeCoordinationService},
 * {@link com.curtisnewbie.module.task.service.TaskService}, and {@link com.curtisnewbie.module.task.service.TaskHistoryService}
 * <br>
 * More on services:
 * <ul>
 * <li>
 * {@link com.curtisnewbie.module.task.service.TaskService} is merely a service for inserting/updating data in database
 * </li>
 * <li>
 * {@link com.curtisnewbie.module.task.service.NodeCoordinationService} is used to coordinate task scheduling
 * between nodes of same cluster, use
 * {@link com.curtisnewbie.module.task.service.NodeCoordinationService#coordinateJobTriggering(com.curtisnewbie.module.task.vo.TaskVo, java.lang.String)}
 * to trigger job if you want it to run 'immediately'.
 * </li>
 * <li>
 * You can also inject {@link com.curtisnewbie.module.task.scheduling.RunningTaskCounter} to get how many tasks
 * are currently running in this node (not for the whole cluster, if current node is not a main node, there won't be
 * any tasks running, so the count will always be 0)
 * </li>
 * <li>
 * For starter, see {@link com.curtisnewbie.module.task.config.DistributedTaskModuleStarter}
 * </li>
 * </ul>
 * <br>
 * Extension point:
 * <ul>
 * <li>
 * {@link com.curtisnewbie.module.task.helper.TaskHelper}
 * </li>
 * <li>
 * {@link com.curtisnewbie.module.task.helper.TaskHistoryHelper}
 * </li>
 * </ul>
 *
 * @author yongjie.zhuang
 */
package com.curtisnewbie.module.task;