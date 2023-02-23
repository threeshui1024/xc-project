package com.xc.media.service.jobhandler;

import com.xc.base.utils.Mp4VideoUtil;
import com.xc.media.model.po.MediaProcess;
import com.xc.media.service.MediaFileService;
import com.xc.media.service.MediaProcessService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class VideoTask {

    @Autowired
    private MediaProcessService mediaProcessService;

    @Autowired
    private MediaFileService mediaFileService;

    @Value("${videoprocess.ffmpegpath}")
    private String ffmpeg_path;

    /**
     * 视频处理任务
     */
    @XxlJob("videoJobHandler")
    public void videoJobHandler() throws Exception {

        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        //查询待处理任务，一次处理的任务数和cpu核心数一样
        List<MediaProcess> mediaProcessesList = mediaProcessService.selectListByShardIndex(shardTotal, shardIndex, 2);
        if (mediaProcessesList == null || mediaProcessesList.size() <= 0) {
            log.debug("查询到的待处理视频数为0");
            return;
        }

        //要处理的任务数
        int size = mediaProcessesList.size();

        //创建size个线程数量的线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(size);

        //计数器
        CountDownLatch countDownLatch = new CountDownLatch(size);

        //遍历mediaProcessesList，将任务放入线程池
        mediaProcessesList.forEach(mediaProcess -> {
            threadPool.execute(() -> {
                //视频处理状态
                String status = mediaProcess.getStatus();
                //保证幂等性
                if ("2".equals(status)) {
                    log.debug("视频已经处理过了，视频信息：{}", mediaProcess);
                    countDownLatch.countDown();//异常时也让计数器减1
                    return;
                }
                //桶
                String bucket = mediaProcess.getBucket();
                //存储路径
                String filePath = mediaProcess.getFilePath();
                //原始视频的md5值
                String fileId = mediaProcess.getFileId();
                //原始文件名称
                String filename = mediaProcess.getFilename();

                //下载文件
                //先创建临时文件，为原始的视频文件
                File originalVideo = null;
                //处理结束的mp4文件
                File mp4Video = null;
                try {
                    originalVideo = File.createTempFile("original", null);
                    mp4Video = File.createTempFile("mp4", ".mp4");
                } catch (IOException e) {
                    log.error("下载待处理的原始文件前创建临时文件失败");
                    countDownLatch.countDown();//异常时也让计数器减1
                    return;
                }
                //将原始视频下载到本地
                try {
                    mediaFileService.downloadFileFromMinIO(originalVideo, bucket, filePath);
                } catch (Exception e) {
                    log.debug("下载原文件过程出错:{},文件信息{}", e.getMessage(), mediaProcess);
                    countDownLatch.countDown();//异常时也让计数器减1
                    return;
                }

                //调用工具类将avi转成mp4
                //转换后mp4文件的名称
                String mp4_name = fileId + ".mp4";
                //转换后mp4文件的路径
                String mp4_path = mp4Video.getAbsolutePath();
                //创建工具类对象
                Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpeg_path, originalVideo.getAbsolutePath(), mp4_name, mp4_path);
                //开始视频转换，成功将返回success
                String result = videoUtil.generateMp4();
                String statusNew = "3";//视频处理后的状态
                String url = null;//最终访问路径
                if ("success".equals(result)) {
                    //转换成功，上传到minIO
                    String objectName = getFilePath(fileId, ".mp4");//上传到minIO的路径
                    try {
                        mediaFileService.addMediaFilesToMinIO(mp4_path, bucket, objectName);//上传到minIO
                    } catch (Exception e) {
                        log.debug("上传文件出错：{}", e.getMessage());
                        countDownLatch.countDown();//异常时也让计数器减1
                        return;
                    }
                    //如果处理成功，status改为2
                    statusNew = "2";
                    url = "/" + bucket + "/" + objectName;
                }

                //记录任务处理结果
                try {
                    mediaProcessService.saveProcessFinishStatus(mediaProcess.getId(), statusNew, fileId, url, result);
                } catch (Exception e) {
                    log.debug("保存任务结果出错:{}", e.getMessage());
                    countDownLatch.countDown();//异常时也让计数器减1
                    return;
                }

                //计数器减1
                countDownLatch.countDown();
            });
        });

        //阻塞到任务执行完成，当countDownLatch计数器归零，这里的阻塞解除
        //等待,给一个充裕的超时时间,防止无限等待，到达超时时间还没有处理完成则结束任务
        countDownLatch.await(30, TimeUnit.MINUTES);
    }

    private String getFilePath(String fileMd5,String fileExt){
        return   fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
    }





}
