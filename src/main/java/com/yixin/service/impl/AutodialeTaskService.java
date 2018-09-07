package com.yixin.service.impl;

import com.yixin.dao.AutodialerTaskMapper;
import com.yixin.dao.AutodialerTimeRangeMapper;
import com.yixin.dao.CreateLogTableMapper;
import com.yixin.job.TaskJob;
import com.yixin.pojo.AutodialerTask;
import com.yixin.pojo.AutodialerTimeRange;
import com.yixin.service.IAutodialeTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class AutodialeTaskService implements IAutodialeTaskService {

    @Autowired
    private AutodialerTaskMapper autodialerTaskMapper;

    @Autowired
    private CreateLogTableMapper createLogTableMapper;

    @Autowired
    private AutodialerTimeRangeMapper autodialerTimeRangeMapper;

    /**
     * 扫描外呼任务表,失效任务删除
     */
    @Override
    public void scanAutodiaTaskTable() {
        //扫描下面的外呼的任务表
        Map<String,Object> paramsMap = new HashMap<>();
        paramsMap.put("destinationExtension","9999");
        paramsMap.put("start",1);
        List<AutodialerTask> list = autodialerTaskMapper.selectAutodialerTaskListByParams(paramsMap);
        //遍历下面的任务
        if (CollectionUtils.isEmpty(list)) {
            //清空 task 下面的任务
            TaskJob.TASK_MAP.clear();
            return;
        }
        for (AutodialerTask autodialerTask:list) {
            //判断任务是否失效
            Integer status = autodialerTask.getStart();
            //外呼号码
            String waihuPhone = autodialerTask.getOriginationCallerIdNumber();
            //外显号码（将外显号码存为map 的key）
            String waixianPhone = autodialerTask.getRemark();
            //两个号码都不能为空
            boolean taskFlag = !StringUtils.isEmpty(waihuPhone) && !StringUtils.isEmpty(waixianPhone);
            if (!taskFlag) {
                continue;
            }
            if (status == 1) {//有效
                Map<String,Object> taskMap = new HashMap<>();
                //打完一个电话停留的时间
                Integer pauseSecond = autodialerTask.getCallPauseSecond();
                pauseSecond = pauseSecond == null ? 15 : pauseSecond; //默认15秒
                taskMap.put("callPauseSecond",String.valueOf(pauseSecond));
                taskMap.put("waihuPhone",waihuPhone);

                Integer groupId = autodialerTask.getDisableDialTimeGroup();
                //默认为1 号分组
                groupId = groupId == null ? 1 : groupId;
                //读取组下面的时间
                Map<String,Object> timeMap = new HashMap<>();
                timeMap.put("groupUuid",groupId);
                List<AutodialerTimeRange> rangeList = autodialerTimeRangeMapper.getAutodialerTimeRangeList(timeMap);
                TaskJob.TASK_NO_TIME.put(waixianPhone,rangeList);

                if (TaskJob.TASK_MAP.containsKey(waixianPhone)) {
                    List<Map<String, Object>> tempList = TaskJob.TASK_MAP.get(waixianPhone);
                    //不存在数据 需要插入的
                    boolean noFlag = true;
                    for (Map<String, Object> map :tempList){
                        //做数据更新
                        if (map.containsKey(waihuPhone)) {
                            map.put("callPauseSecond",String.valueOf(pauseSecond));
                            noFlag = false;
                            break;
                        }
                    }
                    if (noFlag) {
                        //添加数据
                        tempList.add(taskMap);
                        TaskJob.TASK_MAP.put(waixianPhone,tempList);
                    }
                } else {
                    //不存在外呼可以是添加任务
                    List<Map<String,Object>> tmpList = new ArrayList<>();
                    tmpList.add(taskMap);
                    TaskJob.TASK_MAP.put(waixianPhone,tmpList);
                }
            } else {//任务失效
                if (TaskJob.TASK_MAP.containsKey(waixianPhone)) {
                    //获取下面的list(存储 map map 下面存储的是外呼号码和等待时长)
                    List<Map<String,Object>> tempList = TaskJob.TASK_MAP.get(waixianPhone);
                    if (!CollectionUtils.isEmpty(tempList)) {
                        //删除指定外显号码下的外呼号码
                        for (Map<String,Object> map : tempList) {
                            if (map.containsValue(waihuPhone)) {
                                //删除号码
                                tempList.remove(map);
                                break;
                            }
                        }
                    }else {
                        //删除任务
                        TaskJob.TASK_MAP.remove(waihuPhone);
                    }

                }
            }

        }

        TaskJob.TASK_MAP.forEach((k,v) -> {
            createLogTable(k);
        });
    }

    /**
     * 新建 call_cdr_phone日志表
     * @param waixianPhone
     */
    private void createLogTable(String waixianPhone){
        Map<String,Object> map = new HashMap<>();
        map.put("suffix",waixianPhone);
        createLogTableMapper.createLogTable(map);
    }

}
