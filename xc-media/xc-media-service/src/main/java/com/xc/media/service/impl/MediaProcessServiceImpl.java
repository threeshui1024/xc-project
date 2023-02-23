package com.xc.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xc.media.mapper.MediaFilesMapper;
import com.xc.media.mapper.MediaProcessHistoryMapper;
import com.xc.media.mapper.MediaProcessMapper;
import com.xc.media.model.po.MediaFiles;
import com.xc.media.model.po.MediaProcess;
import com.xc.media.model.po.MediaProcessHistory;
import com.xc.media.service.MediaProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class MediaProcessServiceImpl implements MediaProcessService {

    @Autowired
    private MediaProcessMapper mediaProcessMapper;

    @Autowired
    private MediaProcessHistoryMapper mediaProcessHistoryMapper;

    @Autowired
    private MediaFilesMapper mediaFilesMapper;

    @Override
    public List<MediaProcess> selectListByShardIndex(int shardTotal, int shardindex, int count) {
        return mediaProcessMapper.selectListByShardIndex(shardTotal, shardindex, count);
    }

    @Override
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {
        //查询这个任务
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if (mediaProcess == null) {
            log.debug("更新任务状态时此任务：{}为空", taskId);
            return;
        }

        LambdaQueryWrapper<MediaProcess> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MediaProcess::getId, taskId);
        if (status.equals("3")){
            //任务失败
            MediaProcess mediaProcess1 = new MediaProcess();
            mediaProcess1.setStatus("3");
            mediaProcess1.setErrormsg(errorMsg);
            mediaProcess1.setFinishDate(LocalDateTime.now());
            mediaProcessMapper.update(mediaProcess1, queryWrapper);
        }

        //处理成功，更新状态
        if ("2".equals(status)){
            //跟新待处理任务表
            mediaProcess.setStatus("2");
            mediaProcess.setUrl(url);
            mediaProcess.setFinishDate(LocalDateTime.now());
            mediaProcessMapper.updateById(mediaProcess);

            //更新文件表中的url字段
            MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
            mediaFiles.setUrl(url);
            mediaFilesMapper.updateById(mediaFiles);
        }

        //如果处理成功将任务添加到历史记录表
        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        BeanUtils.copyProperties(mediaProcess, mediaProcessHistory);
        mediaProcessHistoryMapper.insert(mediaProcessHistory);

        //处理成功还需删除待处理表的记录
        mediaProcessMapper.deleteById(taskId);


    }
}
