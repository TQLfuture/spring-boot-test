package com.yixin.dao;

import org.apache.ibatis.annotations.SelectProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 可以用来创建数据库表
 */
public interface CreateLogTableMapper {
    Logger logger = LoggerFactory.getLogger(CreateLogTable.class);

    @SelectProvider(type = CreateLogTable.class,method = "createTable")
    public String createLogTable(Map<String,Object> map);

    class CreateLogTable{
        public String createTable(Map<String,Object> map){
            //获取 数据库后缀
            String suffxi = String.valueOf(map.get("suffix"));
            String sql = "CREATE TABLE IF NOT EXISTS `call_cdr_"+suffxi+"` (\n" +
                    "  `phone_num` varchar(20) NOT NULL,\n" +
                    "  `result_id` tinyint(3) DEFAULT NULL,\n" +
                    "  `flow_id` int(5) NOT NULL,\n" +
                    "  `end_point_id` int(6) DEFAULT NULL,\n" +
                    "  `sms_content` varchar(100) DEFAULT NULL,\n" +
                    "  `detail_info` text,\n" +
                    "  `deal_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                    "  PRIMARY KEY (`phone_num`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
            return sql;
        }
    }

}
