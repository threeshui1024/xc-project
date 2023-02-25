package com.xc.content.service.jobhandler;

import com.xc.messagesdk.model.po.MqMessage;
import com.xc.messagesdk.service.MessageProcessAbstract;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 课程发布处理
 */
@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {

    /**
     * 课程发布消息类型
     */
    public static final String MESSAGE_TYPE = "course_publish";

    /**
     * 任务调度入口
     * @throws Exception
     */
    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() throws Exception {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.debug("shardIndex="+shardIndex+",shardTotal="+shardTotal);
        //参数:分片序号、分片总数、消息类型、一次最多取到的任务数量、一次任务调度执行的超时时间
        process(shardIndex,shardTotal,MESSAGE_TYPE,5,60);
    }

    @Override
    public boolean execute(MqMessage mqMessage) {
        return false;
    }
}
