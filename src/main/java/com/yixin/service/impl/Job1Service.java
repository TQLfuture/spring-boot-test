package com.yixin.service.impl;

import com.yixin.constant.Constant;
import com.yixin.dao.NumberTaskMapper;
import com.yixin.job.TaskJob;
import com.yixin.pojo.AutodialerTimeRange;
import com.yixin.pojo.NumberTask;
import com.yixin.service.IJob1Service;
import com.yixin.service.IYixinDialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class Job1Service implements IJob1Service {
    Logger logger = LoggerFactory.getLogger(Job1Service.class);

    @Autowired
    private NumberTaskMapper numberTaskMapper;
    @Autowired
    private IYixinDialService yixinDialService;

    //是不是在课运行的时间内
    private boolean inPermitTime(String waixianPhone){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        boolean resultBool = false;
        if (!CollectionUtils.isEmpty(TaskJob.TASK_NO_TIME)) {
            //时间获取
            List<AutodialerTimeRange> rangeList = TaskJob.TASK_NO_TIME.get(waixianPhone);
            if (!CollectionUtils.isEmpty(rangeList)) {
                String nowTimeStr = simpleDateFormat.format(new Date());
                for (AutodialerTimeRange autodialerTimeRange:rangeList) {
                    Date endTime = autodialerTimeRange.getEndDateTime();
                    Date beginTime = autodialerTimeRange.getEndDateTime();
                    String endTimeStr = simpleDateFormat.format(endTime);
                    String beinTimeStr = simpleDateFormat.format(beginTime);
                    boolean flag = nowTimeStr.compareTo(beinTimeStr)>=0 && nowTimeStr.compareTo(endTimeStr)<0;
                    if (!flag) {
                        //不在这个访问，代表可以进行进行 电话拨打
                        logger.info("外显号 = " + waixianPhone +" 在可以拨打时间内");
                        resultBool = true;
                        break;
                    }

                }
            }
        }
        return resultBool;
    }

    @Override
    public void jobCheck() {
        logger.info("在校验正在拨打号码的情况=====");
        //进行任务检测
        if (CollectionUtils.isEmpty(TaskJob.TASK_MAP)) {
            logger.info("任务没数据======");
            return;
        }
        TaskJob.TASK_MAP.forEach((k,v) -> {
            if (inPermitTime(k)) {
                //获取下面的key
                checkDailState(k,v);
            }
        });

    }

    /**
     * 检查正在拨打号码的状态
     * @param waixianPhone
     * @param value
     */
    private void checkDailState(String waixianPhone,Object value) {
        List<Map<String,Object>> task_list = (List<Map<String, Object>>) value;
        List<Map<String,String>> dail_doing_list = TaskJob.DIAL_DOING_MAP.get(waixianPhone);
        if (CollectionUtils.isEmpty(dail_doing_list)) {
            logger.info("当前 外显任务 = " + waixianPhone +"没有正在拨打的号码");
            return;
        }
        for (Map<String,Object> map : task_list) {
            //获取下面的数据
            String waihuPhone = (String) map.get("waihuPhone");
            Integer pauseSecond = Integer.parseInt(map.get("callPauseSecond").toString());
            String beijiaoPhone = null;
            boolean flag = !StringUtils.isEmpty(waihuPhone) && pauseSecond != null;
            if (!flag) {continue;}
            //查询正在拨打的电话情况
            for (Map<String,String> dailMap : dail_doing_list) {
                if (dailMap.containsKey(waihuPhone)) {
                    //获取key值
                    beijiaoPhone = dailMap.get(waihuPhone);
                    break;
                }
            }
            if (beijiaoPhone != null) {
                String tableName = Constant.NUMBER_TASK_SUFFIX+waixianPhone;
                //查询此号码拨打情况
                Map<String,Object> params = new HashMap<>();
                params.put("phone",beijiaoPhone);
                params.put("tableName",tableName);
                NumberTask numberTask = numberTaskMapper.selectNumberTaskHungup(params);
                if (numberTask != null) {
                    //查看挂断时间
                    Date hangupDate = numberTask.getHangupdate();
                    if (hangupDate != null) {
                        //去除此电话任务
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(hangupDate);
                        calendar.add(Calendar.SECOND,pauseSecond);
                        boolean removeWaihuBool = calendar.getTime().compareTo(new Date()) >= 0;
                        logger.info("检查任务 = "+waixianPhone+" 是够到达去除外呼的时间 = " +removeWaihuBool);
                        if (removeWaihuBool){
                            //去除正在拨打号码
                            TaskJob.DIAL_DOING_MAP.get(waixianPhone).remove(waihuPhone);
                            logger.info("当前正在任务 = " + waixianPhone+" 去除外呼号码 = " + waihuPhone);
                        }
                    }
                }
            }
        }
    }

    /**
     * 此job 用来打电话
     */
    @Override
    public void jodDail(){
        TaskJob.TASK_MAP.forEach((k,v) -> {
            if (inPermitTime(k)) {
                startDail(k,v);
            }
        });
    }

    public void startDail(String waixianPhone,List<Map<String, Object>> value){
        List<Map<String,Object>> task_list = value;
        if (CollectionUtils.isEmpty(task_list)) {
            logger.info("任务还没有被初始化============");
            return;
        }
        List<Map<String,String>> dail_doing_list = TaskJob.DIAL_DOING_MAP.get(waixianPhone);
        if (!CollectionUtils.isEmpty(dail_doing_list)) {
            for (Map<String,Object> map : task_list) {
                String waihuPhone = String.valueOf(map.get("waihuPhone"));
                boolean dailFlag = true;
                for (Map<String,String> dailMap : dail_doing_list) {
                    if (dailMap.containsKey(waihuPhone)) {
                        dailFlag = false;
                        break;
                    }
                }
                if (dailFlag) {
                    yixinDialService.dail(waixianPhone,waihuPhone);
                }
            }

        } else {
            //拨打该任务下所有的号码
            for (Map<String,Object> map : task_list) {
                String waihuPhone = String.valueOf(map.get("waihuPhone"));
                logger.info("外显任务 = " + waixianPhone+" 没有正在拨打的号码，开始拨打 = " +waihuPhone );
                yixinDialService.dail(waixianPhone,waihuPhone);
            }
        }
    }



}
