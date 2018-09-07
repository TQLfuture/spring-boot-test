package com.yixin.initialize;

import com.yixin.dao.AutodialerTaskMapper;
import com.yixin.pojo.AutodialerTask;
import com.yixin.service.impl.AutodialeTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//容器加载完成执行
public class ApplicationStartupAfter implements ApplicationListener<ContextRefreshedEvent> {

    Logger logger = LoggerFactory.getLogger(ApplicationStartupAfter.class);

    private AutodialeTaskService autodialeTaskService;


    /**
     * spring boot项目下，上面方式正确触发执行一次；如果是spring web项目下，可能会造成二次执行，因为此时系统会存在两个容器，
     * 一个是spring本身的root application context，
     * 另一个是servlet容器（作为spring容器的子容器，projectName-servlet context），此时，加以下限制条件规避：
     * @param event
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if(event.getApplicationContext().getParent() == null){
            logger.info("容器启动之后");
//            //获取到对象
            autodialeTaskService = event.getApplicationContext().getBean(AutodialeTaskService.class);
            Map<String,Object> params = new HashMap<>();
            params.put("destinationExtension","9999");
            autodialeTaskService.scanAutodiaTaskTable();
        }

    }

}
