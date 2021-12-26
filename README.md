# distributed-task-module

Module for distributed task scheduling


## Middleware

- MySQL
- Redis

## Configuration

Data Type | Property Name | Description | Default Value
--------- | ------------- | ----------- | --------------
String | distributed-task-module.application-group | name of application group, a cluster should share the same name such that they are managed together | default
boolean | distributed-task-module.enabled | whether this module is enabled | true
boolean | distributed-task-module.scheduling.disabled | whether scheduling is disabled for current node. When it's set to false, it will not attempt to be become the main node. I.e., it will not run any task. However, it will still be able to request triggering a task, though the triggered task is still run by the main node | false

## Modules and Dependencies

This project depends on the following modules that you must manually install (using `mvn clean install`).

- curtisnewbie-bom
    - description: BOM file for dependency management
    - url: https://github.com/CurtisNewbie/curtisnewbie-bom
    - branch: main
    - under `/microservice` folder

- common-module
    - description: for common utility classes 
    - url: https://github.com/CurtisNewbie/common-module
    - branch: main

- redis-util-module
    - description: Utility classes for Redis
    - url: https://github.com/CurtisNewbie/redis-util-module
    - branch: main
