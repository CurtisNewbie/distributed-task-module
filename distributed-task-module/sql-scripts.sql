
CREATE TABLE task (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT "id",
    job_name VARCHAR(255) NOT NULL COMMENT "job's name",
    target_bean VARCHAR(255) NOT NULL COMMENT "name of bean that will be executed",
    cron_expr VARCHAR(255) NOT NULL COMMENT "cron expression",
    app_group VARCHAR(255) NOT NULL COMMENT "app group that runs this task",
    last_run_start_time TIMESTAMP COMMENT "the last time this task was executed",
    last_run_end_time TIMESTAMP COMMENT "the last time this task was finished",
    last_run_by VARCHAR(255) COMMENT "app that previously ran this task",
    last_run_result VARCHAR(255) COMMENT "result of last execution",
    enabled INT NOT NULL DEFAULT 0 COMMENT "whether the task is enabled: 0-disabled, 1-enabled",
    concurrent_enabled INT NULL DEFAULT 0 COMMENT "whether the task can be executed concurrently: 0-disabled, 1-enabled"
) ENGINE=InnoDB COMMENT "task";