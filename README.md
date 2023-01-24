# distributed-task-module v2.1.1.4

Module for simple distributed task scheduling :D

### Plugins

- `dtask-go` is a simple golang service that acts as a centralized manager for managing tasks and task histories. It's completely optional. It's under repository (https://github.com/CurtisNewbie/dtask-go)
.
### Requirement 

- MySQL
- Redis

### Configuration

| Data Type | Property Name                                    | Description                                                                                                                                                                                                                                                                  | Default Value |
|-----------|--------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------|
| String    | distributed-task-module.application-group        | Name of application group, a cluster should share the same name such that they are managed together.                                                                                                                                                                         | default       |
| boolean   | distributed-task-module.enabled                  | Whether this module is enabled. When disabled, beans are not populated, this is good when you are running unit tests only.                                                                                                                                                   | true          |
| boolean   | distributed-task-module.scheduling.disabled      | Whether scheduling is disabled for current node. When it's set to false, it will not attempt to become the main node. I.e., it will not run any task. However, it will still be able to request triggering a task, though the triggered task is still run by the main node.  | false         |
| boolean   | distributed-task-module.plugin.dtask-go.enabled  | Enable dtask-go plugin. This plugin is responsible for connecting `dtask-go` service to fetch tasks, update tasks, and record task histories. By default this is disabled, and this module will instead look for tables `task` and `task_history` in the connected database. | false         |
| String    | distributed-task-module.plugin.dtask-go.base-url | Base url of `dtask-go` service. For example, `http://localhost:8083/remote/`                                                                                                                                                                                                 |               |

### Modules and Dependencies

This project depends on the following modules that you must manually install (using `mvn clean install`).

- [curtisnewbie-bom](https://github.com/CurtisNewbie/curtisnewbie-bom)
- [common-module v2.1.9](https://github.com/CurtisNewbie/common-module/tree/v2.1.9)
- [redis-util-module v2.0.3](https://github.com/CurtisNewbie/redis-util-module/tree/v2.0.3)

### Declaration of Tasks on Startup 

The annotation `@JobDeclaration` can be used to declare tasks automatically on application startup, this frees you from preparing the SQL when a new job is added.

```java
/**
 * My Special Job
 *
 * @author yongj.zhuang
 */
@Component
@JobDeclaration(name = "my special job", cron = "0 0 0/1 * * ?", concurrent = false, enabled = true, overridden = true)
public class SpecialJob extends AbstractJob {
    @Override
    protected void executeInternal(TaskVo task) throws JobExecutionException {
        // do something
    }
}
```
