# distributed-task-module

Module for distributed task scheduling


## Middleware

- MySQL
- Redis

## Configuration

Data Type | Property Name | Description | Default Value
--------- | ------------- | ----------- | --------------
String | distributed-task-module.application-group | name of application group, a cluster should share the same name such that they are managed together | default
boolean | distributed-task-module.enabled | this configures whether current node enables distributed task scheduling, when this value is set to false, the node will not try to be the main node, and thus will not run any task at all. However, it will still be able to request triggering a task (i.e., run the selected task immediately), though the triggered task is still run by the main node | true

## Modules and Dependencies

This project depends on the following modules that you must manually install (using `mvn clean install`).

- curtisnewbie-bom
    - description: BOM file for dependency management
    - url: https://github.com/CurtisNewbie/curtisnewbie-bom
    - branch: main
    - version: micro-0.0.1 (under `/microservce` folder)

- common-module
    - description: for common utility classes 
    - url: https://github.com/CurtisNewbie/common-module
    - branch: main

- redis-util-module
    - description: Utility classes for Redis
    - url: https://github.com/CurtisNewbie/redis-util-module
    - branch: main