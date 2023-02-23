package com.xc.media.service;

import com.xc.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MediaProcessService {

    /**
     * 根据分片参数获取待处理任务
     * @param shardTotal 分片总数
     * @param shardindex 分片序号
     * @param count 任务数
     * @return
     */
    List<MediaProcess> selectListByShardIndex(int shardTotal, int shardindex, int count);


    /**
     * 将url存储至数据，并更新状态为成功，并将待处理视频记录删除存入历史
     * @param taskId 任务id
     * @param status 处理结果，2:成功3失败
     * @param fileId 文件id
     * @param url 文件访问url
     * @param errorMsg 失败原因
     */
    void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg);

}
