package com.yixin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yixin.constant.Constant;
import com.yixin.dao.NumberTaskMapper;
import com.yixin.job.TaskJob;
import com.yixin.pojo.NumberTask;
import com.yixin.service.IYixinDialService;
import com.yixin.until.EncryptUtils;
import com.yixin.until.SSLClientUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Service
public class YixinDialService implements IYixinDialService {

    Logger logger = LoggerFactory.getLogger(YixinDialService.class);

    // 开发者账号 accountSid，需要替换为开发者真正的值
    private static final String YIXIN_ACCOUNTSID = "";
    // 开发者账号的token，需要替换为开发者真正的值
    private static final String YIXIN_TOKEN = "";
    //测试 appid //应用appId，需要替换为开发者真正的值
    private static final String YIXIN_APPID = "";
    private static final String YIXIN_URL = "https://naas.ecplive.cn/api/exec/Account/"+YIXIN_ACCOUNTSID+"/Call";

    private static CloseableHttpClient YIXIN_CLIENT = SSLClientUtils.createSSLInsecureClient();
    private static RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(3000).build();



    @Autowired
    private NumberTaskMapper numberTaskMapper;

    @Override
    public void dail(String waixianPhone,String waihuPhone) {
        //查询任务
        if (StringUtils.isEmpty(waixianPhone)) {
            logger.info("外呼号码为空============");
            return;
        }
        String tableName = Constant.NUMBER_TASK_SUFFIX+waixianPhone;
        //获取被叫号码进行呼叫
        NumberTask numberTask = numberTaskMapper.selectValidNumberTask(tableName);
        if (numberTask != null) {
            //获取客户号码（被叫号码）
            String beijiaoPhone = numberTask.getNumber();
            if (!StringUtils.isEmpty(beijiaoPhone)){
                //进行呼叫
                boolean flag = this.yixinDialRequest(waihuPhone,beijiaoPhone,waixianPhone);
                if(flag){
                    logger.info("翼信正在请求，请求参数 主叫 = "+waihuPhone+" 被叫 = " + beijiaoPhone +" 外呼 = " + waixianPhone+"请求成功");
                    //存入正在拨打的被叫号码
                    saveDailPhoneDoing(waixianPhone,waihuPhone,beijiaoPhone);
                    //进行字段更新
                    Map<String,Object> paramsMap = new HashMap<>();
                    paramsMap.put("state","1"); //正在通话中
                    paramsMap.put("phone",beijiaoPhone);
                    paramsMap.put("callDate","1"); //创建时间
                    paramsMap.put("tableName",tableName);
                    numberTaskMapper.updateNumberTaskStatus(paramsMap);
                } else {
                    //三方请求失败
                    logger.info("翼信正在请求，请求参数 主叫 = "+waihuPhone+" 被叫 = " + beijiaoPhone +" 外呼 = " + waixianPhone+"请求失败，去除任务");
                    Map<String,Object> paramsMap = new HashMap<>();
                    paramsMap.put("state","5"); //请求失败
                    paramsMap.put("phone",beijiaoPhone);
                    paramsMap.put("callDate","1"); //创建时间
                    paramsMap.put("tableName",tableName);
                    numberTaskMapper.updateNumberTaskStatus(paramsMap);
                    //去除任务
                    TaskJob.DIAL_DOING_MAP.get(waixianPhone).remove(waihuPhone);
                }
            }
        }
    }

    /**
     * 存储正在拨打的号码
     */
    private void saveDailPhoneDoing(String waixianPhone,String waihuPhone,String beijiaoPhone){
        List<Map<String,String>> tmpList = null;

        Map<String,String> tempMap = new HashMap<>();
        tempMap.put("waihuPhone",waihuPhone);
        tempMap.put("beijiaoPhone",beijiaoPhone);

        boolean containsBool = TaskJob.DIAL_DOING_MAP.containsKey(waixianPhone);
        if (containsBool) {
            //获取list
            tmpList = TaskJob.DIAL_DOING_MAP.get(waixianPhone);
            if (!CollectionUtils.isEmpty(tmpList)) {
                boolean beijiaoBool = true;
                for (Map<String,String> map : tmpList) {
                    if (map.containsValue(beijiaoPhone)) {
                        beijiaoBool = false;
                        break;
                    }
                }
                //如果不存在就存储外呼号码与被叫号码
                if (beijiaoBool) {
                    tmpList.add(tempMap);
                }
            } else {
                tmpList = new ArrayList<>();
                tmpList.add(tempMap);
            }
        } else {
            tmpList = new ArrayList<>();
            tmpList.add(tempMap);
        }
        TaskJob.DIAL_DOING_MAP.put(waixianPhone,tmpList);
    }


    /**
     * 向第三方发送请求
     * @param zhujiaoPhone
     * @param beijiaoPhone
     * @return
     */
    private boolean yixinDialRequest(String zhujiaoPhone,String beijiaoPhone,String waixianPhone)  {
        String result = null;
        RequestBuilder requestBuilder = RequestBuilder.post().setUri(YIXIN_URL);
        requestBuilder.setConfig(requestConfig);
        long ts = System.currentTimeMillis();
        requestBuilder.addHeader("ts", ts + "");
        requestBuilder.addHeader("apiId", UUID.randomUUID().toString());
        requestBuilder.addHeader("accountSid", YIXIN_ACCOUNTSID);
        requestBuilder.addHeader("sign", EncryptUtils.md5(YIXIN_ACCOUNTSID + YIXIN_TOKEN + ts));

        requestBuilder.addParameter("from", zhujiaoPhone);
        requestBuilder.addParameter("to", beijiaoPhone);
        requestBuilder.addParameter("appId", YIXIN_APPID);

        //requestBuilder.addHeader("toDisplay",waixianPhone);

        HttpUriRequest req = requestBuilder.build();
        try {
            CloseableHttpResponse resp = YIXIN_CLIENT.execute(req);
            int statusCode = resp.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new RuntimeException("HTTP响应错误，statusCode:" + statusCode);
            }
            HttpEntity entity = resp.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity, "utf-8");
            }
            EntityUtils.consume(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //进行字符串解析,是否请求成功
        logger.info("响应结果报文 = " + result);
        boolean flag = parseYixinResultFlagBool(result);
        return flag;
    }

    /**
     * 解析第三方返回的结果
     * @param result
     */
    private boolean parseYixinResultFlagBool(String result){
        boolean flag = false;
        //空字符串
        if (StringUtils.isEmpty(result)) { return flag; }
        try {
            JSONObject jsonObj = JSON.parseObject(result);
            //获取结果
            if (jsonObj.containsKey("code")) {
                String code = jsonObj.getString("code");
                if (code != null && code.equals("200")) {
                    //获取rows
                    JSONArray array = jsonObj.getJSONArray("rows");
                    if (array != null && array.size() == 1) {
                        //请求成功
                        flag = true;
                    }else {
                        //获取的数据字段rows 异常
                        logger.info("获取rows字段异常 = " + result);
                    }
                } else {
                    logger.info("非200响应 = "+ result);
                }
            }
        }catch (Exception e) {
            logger.error("解析异常 = " + result);
        }
        return flag;
    }

}
