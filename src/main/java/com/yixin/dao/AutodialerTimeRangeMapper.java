package com.yixin.dao;

import com.yixin.pojo.AutodialerTimeRange;

import java.util.List;
import java.util.Map;

public interface AutodialerTimeRangeMapper {

    public List<AutodialerTimeRange> getAutodialerTimeRangeList(Map<String,Object> map);
}
