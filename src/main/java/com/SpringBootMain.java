package com;

import com.yixin.initialize.ApplicationStartupAfter;
import com.yixin.initialize.ApplicationStartupBefore;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling //开启定时任务
@MapperScan("com.yixin.dao")
public class SpringBootMain {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SpringBootMain.class);

        //初始化加载
//        List<ApplicationContextInitializer> list = new ArrayList<>();
//        list.add(new LoadDataService());

        //spring 容器初始化之前
        application.addInitializers(new ApplicationStartupBefore());
        //spring 容器初始化之后
        application.addListeners(new ApplicationStartupAfter());

        application.run(args);
        //SpringApplication.run(SpringBootMain.class);
    }
}
