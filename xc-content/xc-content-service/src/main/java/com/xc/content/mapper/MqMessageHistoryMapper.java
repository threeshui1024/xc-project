package com.xc.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xc.content.model.po.MqMessageHistory;
import org.apache.ibatis.annotations.Mapper;

/**
* @author DELL
* @description 针对表【mq_message_history】的数据库操作Mapper
* @createDate 2023-02-26 00:39:10
* @Entity com.xc.content.model.po.MqMessageHistory
*/
@Mapper
public interface MqMessageHistoryMapper extends BaseMapper<MqMessageHistory> {

}




