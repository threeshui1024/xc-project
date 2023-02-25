package com.xc.content.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xc.content.mapper.MqMessageMapper;
import com.xc.content.model.po.MqMessage;
import com.xc.content.service.MqMessageService;
import org.springframework.stereotype.Service;

/**
* @author DELL
* @description 针对表【mq_message】的数据库操作Service实现
* @createDate 2023-02-26 00:39:10
*/
@Service
public class MqMessageServiceImpl extends ServiceImpl<MqMessageMapper, MqMessage>
    implements MqMessageService {

}




