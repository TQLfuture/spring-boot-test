package com.yixin.dao;

import com.yixin.pojo.NumberTask;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface NumberTaskMapper {

    /**
     * 查找未拨打的被叫电话
     * @param tableName
     * @return
     */
    public NumberTask selectValidNumberTask(@Param(value = "tableName") String tableName);

    /**
     * 更新字段
     * @param tableName
     * @param params
     * @return
     */
    public Integer updateNumberTaskStatus(Map<String,Object> params);

    /**
     * 查找正在处于通话中的被叫号码
     * @param tableName
     * @return
     */
    public NumberTask selectDialPhoneDoing(@Param(value = "tableName") String tableName);

    /**
     * 查看被叫号码的拨打状态
     * @param tableName
     * @param params
     * @return
     */
    public NumberTask selectNumberTaskHungup(Map<String,Object> params);
}
