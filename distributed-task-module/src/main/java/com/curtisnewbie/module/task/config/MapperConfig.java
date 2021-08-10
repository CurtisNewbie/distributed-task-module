package com.curtisnewbie.module.task.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author yongjie.zhuang
 */
@Configuration
@MapperScan("com.curtisnewbie.module.task.dao")
public class MapperConfig {
}
