package com.yixin.job;

import com.yixin.pojo.AutodialerTimeRange;
import com.yixin.service.IJob1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("taskJob")
public class TaskJob {

     //用来存储任务
     public static final Map<String, List<Map<String, Object>>> TASK_MAP = new HashMap<>();
     //存储正在拨打的号码
     public static final Map<String, List<Map<String, String>>> DIAL_DOING_MAP = new HashMap<>();
     //存储禁止任务执行的时间
     public static final Map<String, List<AutodialerTimeRange>> TASK_NO_TIME = new HashMap<>();

     @Autowired
     private IJob1Service job1Service;


     //定时器将在5秒后，每隔三秒启动一次
     @Scheduled(initialDelay = 5000, fixedDelay = 3000)
     public void jobDail() {
          job1Service.jodDail();
     }

     //检查任务状态
     @Scheduled(initialDelay = 10000, fixedDelay = 3000)
     public void jobCheck() {
          job1Service.jobCheck();
     }

}