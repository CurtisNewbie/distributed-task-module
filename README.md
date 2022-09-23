# distributed-task-module

Module for simple distributed task scheduling :D

### Plugins

- `dtask-go` is a simple golang service that acts as a centralized manager for tasks and task histories. 

### Requirement 

- MySQL
- Redis

### Configuration

Data Type | Property Name | Description | Default Value
--------- | ------------- | ----------- | --------------
String | distributed-task-module.application-group | Name of application group, a cluster should share the same name such that they are managed together. | default
boolean | distributed-task-module.enabled | Whether this module is enabled. When disabled, beans are not populated, this is good when you are running unit tests only. | true
boolean | distributed-task-module.scheduling.disabled | Whether scheduling is disabled for current node. When it's set to false, it will not attempt to become the main node. I.e., it will not run any task. However, it will still be able to request triggering a task, though the triggered task is still run by the main node. | false
boolean | distributed-task-module.plugin.dtask-go.enabled | Enable dtask-go plugin. This plugin is responsible for connecting `dtask-go` service to fetch tasks, update tasks, and record task histories. By default this is disabled, and this module will instead look for tables `task` and `task_history` in the connected database. | false
String | distributed-task-module.plugin.dtask-go.base-url | Base url of `dtask-go` service. For example, `http://localhost:8083/remote/` | 

### Modules and Dependencies

This project depends on the following modules that you must manually install (using `mvn clean install`).

- [curtisnewbie-bom](https://github.com/CurtisNewbie/curtisnewbie-bom)
- [common-module v2.1.7](https://github.com/CurtisNewbie/common-module/tree/v2.1.7)
- [redis-util-module v2.0.3](https://github.com/CurtisNewbie/redis-util-module/tree/v2.0.3)