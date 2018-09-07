package com.yixin.test.yixin;

import com.SpringBootMain;
import com.yixin.dao.AutodialerTaskMapper;
import com.yixin.dao.AutodialerTimeRangeMapper;
import com.yixin.dao.CreateLogTableMapper;
import com.yixin.dao.NumberTaskMapper;
import com.yixin.pojo.AutodialerTask;
import com.yixin.pojo.AutodialerTimeRange;
import com.yixin.pojo.NumberTask;
import com.yixin.service.IAutodialeTaskService;
import com.yixin.service.IJob1Service;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootMain.class})
public class MybatisTest {

    Logger logger = LoggerFactory.getLogger(MybatisTest.class);

    @Autowired
    private AutodialerTaskMapper autodialerTaskMapper;
    @Autowired
    private CreateLogTableMapper createLogTableMapper;
    @Autowired
    private IAutodialeTaskService autodialeTaskService;
    @Autowired
    private IJob1Service job1Service;
    @Autowired
    private AutodialerTimeRangeMapper autodialerTimeRangeMapper;

    @Test
    public void test(){
        Map<String,Object> params = new HashMap<>();
        params.put("destinationExtension","9999");
        List<AutodialerTask> list = autodialerTaskMapper.selectAutodialerTaskListByParams(params);
        logger.info(list.toString());
    }

    @Test
    public void test2(){
        Map<String,Object> params = new HashMap<>();
        params.put("destinationExtension","9999");
        createLogTableMapper.createLogTable(params);
    }

    @Test
    public void test3(){
        autodialeTaskService.scanAutodiaTaskTable();
    }



    @Test
    public void test5(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Map<String,Object> timeMap = new HashMap<>();
        timeMap.put("groupUuid",1);
        List<AutodialerTimeRange> list = autodialerTimeRangeMapper.getAutodialerTimeRangeList(timeMap);

        String nowTimeStr = simpleDateFormat.format(new Date());
        boolean resultBool = false;
        for (AutodialerTimeRange autodialerTimeRange:list) {

            Date endTime = autodialerTimeRange.getEndDateTime();
            Date beginTime = autodialerTimeRange.getEndDateTime();
            String endTimeStr = simpleDateFormat.format(endTime);
            String beinTimeStr = simpleDateFormat.format(beginTime);
            boolean flag = nowTimeStr.compareTo(beinTimeStr)>=0 && nowTimeStr.compareTo(endTimeStr)<0;
            if (!flag) {
                //不在这个访问，代表可以进行进行 电话拨打
                resultBool = true;
                break;
            }
        }
        logger.info("aaa = " + resultBool);
    }

    @Autowired
    private NumberTaskMapper numberTaskMapper;

    @Test
    public void test6(){
        Map<String,Object> paramsMap = new HashMap<>();
        String tableName = "number_057185203582";
        paramsMap.put("state","1"); //正在通话中
        paramsMap.put("phone","17756602705");
        paramsMap.put("callDate","1"); //创建时间
        paramsMap.put("tableName",tableName);
        numberTaskMapper.updateNumberTaskStatus(paramsMap);
    }
}
