package com.yixin.dao;

import com.yixin.pojo.AutodialerTask;

import java.util.List;
import java.util.Map;

public interface AutodialerTaskMapper {

    public List<AutodialerTask> selectAutodialerTaskListByParams(Map<String,Object> map);

}
