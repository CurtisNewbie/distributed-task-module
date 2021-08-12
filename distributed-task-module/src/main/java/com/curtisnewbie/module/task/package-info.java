/**
 * <h1>
 * Distributed Task Module
 * </h1>
 * <p>
 * To use this module, make sure you properly configure your database and redis.
 * <p>
 * <br>
 * Then, all you have to do is to create your {@link org.quartz.Job} instances, and make them spring managed beans. And
 * configure your {@code task} table, set the {@code target_bean} field using the same name as the bean, as well as the
 * cron expression and 'group' property (which is the name of your cluster). It should work just fine.
 * </p>
 * <br>
 * <p>
 * For example, our job is declared as a spring managed bean with bean name 'physicalDeletingFileJob' (this should be
 * same as the target_bean).
 * <pre>
 * {@code
 * @Component
 * public class PhysicalDeletingFileJob implements Job {
 *      //...
 * }
 * }
 * </pre>
 * <p>
 * Then in our table, we have
 * <ul>
 *    <li>id=1</li>
 *    <li>name='fileDeleteJob'</li>
 *    <li>target_bean='physicalDeletingFileJob'</li>
 *    <li>cron_expr='0 0/2 * ? * *'</li>
 *    <li>group='file-server'</li>
 * </ul>
 *
 * <br>
 * <p>
 * Finally, in our property file, we configure the following property using the same value as the group in database.
 * </p>
 * <pre>
 * {@code
 * # name of the application group (same cluster should share the same name)
 * distributed-task-module.application-group=file-server
 * }
 * </pre>
 * <br>
 * More on services:
 * <ul>
 * <li>
 * {@link com.curtisnewbie.module.task.service.TaskService} is merely a service for inserting/updating data in databse
 * </li>
 * <li>
 * {@link com.curtisnewbie.module.task.service.NodeCoordinationService} is used to coordinate task scheduling
 * between nodes of same cluster, use
 * {@link com.curtisnewbie.module.task.service.NodeCoordinationService#coordinateJobTriggering(com.curtisnewbie.module.task.vo.TaskVo)}
 * to trigger job if you want it to run immediately.
 * </li>
 * <li>
 * You can also inject {@link com.curtisnewbie.module.task.scheduling.RunningTaskCounter} to get how many tasks
 * are currently running in this node (not for the whole cluster, if current node is not a main node, there won't be
 * any tasks running)
 * </li>
 * </ul>
 *
 * @author yongjie.zhuang
 */
package com.curtisnewbie.module.task;