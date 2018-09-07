package com.yixin.initialize;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * spring 容器启动前加载的数据
 */
public class ApplicationStartupBefore implements ApplicationContextInitializer {

    Logger logger = LoggerFactory.getLogger(ApplicationStartupBefore.class);


    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        logger.info("初始化加载一次");

    }
}
