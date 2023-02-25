package com.xc.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xc.content.model.po.MqMessage;
import org.apache.ibatis.annotations.Mapper;

/**
* @author DELL
* @description 针对表【mq_message】的数据库操作Mapper
* @createDate 2023-02-26 00:39:10
* @Entity com.xc.content.model.po.MqMessage
*/
@Mapper
public interface MqMessageMapper extends BaseMapper<MqMessage> {

}




