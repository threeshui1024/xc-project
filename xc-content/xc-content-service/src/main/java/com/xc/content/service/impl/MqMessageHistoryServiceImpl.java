package com.xc.content.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xc.content.mapper.MqMessageHistoryMapper;
import com.xc.content.model.po.MqMessageHistory;
import com.xc.content.service.MqMessageHistoryService;
import org.springframework.stereotype.Service;

/**
* @author DELL
* @description 针对表【mq_message_history】的数据库操作Service实现
* @createDate 2023-02-26 00:39:10
*/
@Service
public class MqMessageHistoryServiceImpl extends ServiceImpl<MqMessageHistoryMapper, MqMessageHistory>
    implements MqMessageHistoryService {

}




