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
 * For example, in our code:
 * </p>
 * <pre>
 * {@code
 * @Component
 * public class PhysicalDeletingFileJob implements Job {
 *      //...
 * }
 * }
 * </pre>
 *
 * <p>
 * In our table:
 * </p>
 * <pre>
 * <table>
 * <tr>
 * <td>id</td>
 * <td>name</td>
 * <td>target_bean</td>
 * <td>cron_expr</td>
 * <td>group</td>
 * </tr>
 * <tr>
 * <td>1 </td>
 * <td>fileDeleteJob</td>
 * <td>physicalDeletingFileJob</td>
 * <td>0 0/2 * ? * *</td>
 * <td>file-server</td>
 * </tr>
 * </table>
 * </pre>
 *
 * <br>
 * <p>
 * Finally, in our property file:
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
 * {@link com.curtisnewbie.module.task.service.NodeCoordinationService#coordinateJobTriggering(com.curtisnewbie.module.task.dao.TaskEntity)}
 * to trigger job if you want it to run immediately.
 * </li>
 * </ul>
 *
 * @author yongjie.zhuang
 */
package com.curtisnewbie.module.task;