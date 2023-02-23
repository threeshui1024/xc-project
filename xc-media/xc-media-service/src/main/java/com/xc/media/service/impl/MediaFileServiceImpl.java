package com.xc.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xc.base.execption.XcException;
import com.xc.base.model.PageParams;
import com.xc.base.model.PageResult;
import com.xc.base.model.RestResponse;
import com.xc.media.mapper.MediaFilesMapper;
import com.xc.media.mapper.MediaProcessMapper;
import com.xc.media.model.dto.QueryMediaParamsDto;
import com.xc.media.model.dto.UploadFileParamsDto;
import com.xc.media.model.dto.UploadFileResultDto;
import com.xc.media.model.po.MediaFiles;
import com.xc.media.model.po.MediaProcess;
import com.xc.media.service.MediaFileService;
import io.minio.*;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2022/9/10 8:58
 */
@Slf4j
@Service
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    MediaFilesMapper mediaFilesMapper;

    @Autowired
    MinioClient minioClient;

    @Autowired
    private MediaProcessMapper mediaProcessMapper;

    //普通文件存储的桶
    @Value("${minio.bucket.files}")
    private String bucket_files;

    //视频文件存储的桶
    @Value("${minio.bucket.videofiles}")
    private String bucket_videos;

    @Autowired
    private MediaFileService currentProxy;

    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(queryMediaParamsDto.getFilename()), MediaFiles::getFilename, queryMediaParamsDto.getFilename());
        queryWrapper.eq(StringUtils.isNotEmpty(queryMediaParamsDto.getFileType()), MediaFiles::getFileType, queryMediaParamsDto.getFileType());

        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;

    }

    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, byte[] bytes, String folder, String objectName) {

        //得到文件的md5值
        String fileMd5 = DigestUtils.md5Hex(bytes);

        //如果folder和objectName为空，则自动生成
        if (StringUtils.isEmpty(folder)) {
            //自动生成目录的路径，按年月日生成
            folder = getFileFolder(new Date(), true, true, true);
        }else if (folder.indexOf("/") < 0){ //查看路径结尾是否有分隔符
            folder = folder + "/";
        }
        //文件名称
        String filename = uploadFileParamsDto.getFilename();

        if(StringUtils.isEmpty(objectName)){
            //如果objectName为空，使用文件的md5值为objectName
            objectName = fileMd5 + filename.substring(filename.lastIndexOf("."));
        }


        objectName = folder + objectName;//minIO里面的objectName
        try {
            //将文件上传到分布式文件系统
            addMediaFilesToMinIO(bytes, bucket_files, objectName);

            //保存到数据库,这里使用自身的代理对象来调用方法，以便事务生效
            MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_files, objectName);

            //准备返回数据
            UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
            BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
            return uploadFileResultDto;
        } catch (Exception e) {
            log.error("上传文件失败：{}", e.getMessage());
            throw new RuntimeException("上传文件失败");
        }
    }

    /**
     * 保存文件信息到数据库
     * @param companyId 机构id
     * @param fileMd5 主键
     * @param uploadFileParamsDto 文件信息
     * @param bucket 桶名称
     * @param objectName 文件名称
     * @return
     */
    @Transactional
    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();

            //封装数据
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(fileMd5);
            mediaFiles.setFileId(fileMd5);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);

            //获取扩展名
            String extension = null;
            String fileName = uploadFileParamsDto.getFilename();
            if (StringUtils.isNotEmpty(fileName) && fileName.indexOf(".") >= 0) {
                extension = fileName.substring(fileName.lastIndexOf("."));
            }
            //媒体类型
            String mimeType = getMimeTypeByExtension(extension);
            //图片、mp4视频可以设置url
            if (mimeType.indexOf("image") >= 0 || mimeType.indexOf("mp4") >= 0) {
                mediaFiles.setUrl("/" + bucket + "/" + objectName);
            }
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setStatus("1");
            mediaFiles.setAuditStatus("002003");
            //插入文件表
            mediaFilesMapper.insert(mediaFiles);

            //将avi视频添加到待处理任务表
            if (mimeType.equals("video/x-msvideo")){
                MediaProcess mediaProcess = new MediaProcess();
                BeanUtils.copyProperties(mediaFiles, mediaProcess);
                //设置一个状态
                mediaProcess.setStatus("1");//未处理
                mediaProcessMapper.insert(mediaProcess);
            }


        }
        return mediaFiles;
    }

    @Override
    public RestResponse<Boolean> checkfile(String fileMd5) {
        //在文件表存在，并且在文件系统存在，此文件才存在
        //1.检查在文件表是否存在
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            return RestResponse.success(false);
        }

        //2.检查在文件系统是否存在
        GetObjectArgs build = GetObjectArgs.builder().bucket(mediaFiles.getBucket()).object(mediaFiles.getFilePath()).build();
        try {
            InputStream inputStream = minioClient.getObject(build);
            if (inputStream == null) { //文件不存在
                return RestResponse.success(false);
            }
        } catch (Exception e) {
            return RestResponse.success(false);
        }

        //查询成功
        return RestResponse.success(true);
    }

    @Override
    public RestResponse<Boolean> checkchunk(String fileMd5, int chunk) {
        //拿到分块文件所在目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //分块文件的完整路径
        String path = chunkFileFolderPath + chunk;

        //查询文件系统分块文件是否存在
        //查看是否在文件系统存在
        GetObjectArgs build = GetObjectArgs.builder().bucket(bucket_videos).object(path).build();
        try {
            InputStream inputStream = minioClient.getObject(build);
            if (inputStream == null) { //文件不存在
                return RestResponse.success(false);
            }
        } catch (Exception e) {
            return RestResponse.success(false);
        }

        //查询成功
        return RestResponse.success(true);
    }

    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk, byte[] bytes) {
        //得到分块文件的目录路径
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //得到分块文件的完整路径
        String chunkFilePath = chunkFileFolderPath + chunk;

        try {
            //将文件存储至minIO
            addMediaFilesToMinIO(bytes, bucket_videos,chunkFilePath);
        } catch (Exception ex) {
            ex.printStackTrace();
            XcException.cast("上传过程出错请重试");
        }
        return RestResponse.success();
    }

    @Override
    public RestResponse mergechunks(Long companyId, String fileMd5,int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {

        //1.下载分块
        File[] files = checkChunkStatus(fileMd5, chunkTotal);

        //2.合并分块
        //得到合并后文件的扩展名
        String filename = uploadFileParamsDto.getFilename();
        //扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        //创建一个临时文件用于合并文件
        File tempMergeFile = null;
        try {
            try {
                tempMergeFile = File.createTempFile("merge", extension);
            } catch (IOException e) {
                throw new XcException("创建临时合并文件出错");
            }

            //创建合并文件的流对象
            try (
                    RandomAccessFile raf_write = new RandomAccessFile(tempMergeFile, "rw");
            ) {
                byte[] b = new byte[1024];
                for (File file : files) {
                    //读取分块文件的流对象
                    try (
                            RandomAccessFile raf_read = new RandomAccessFile(file, "r");
                    ) {
                        int len = -1;
                        while ((len = raf_read.read(b)) != -1) {
                            //向合并文件写数据
                            raf_write.write(b, 0, len);
                        }
                    }
                }
            } catch (IOException e) {
                throw new XcException("文件合并过程出错");
            }

            //校验合并后的文件是否正确
            try {
                FileInputStream mergeFileStream = new FileInputStream(tempMergeFile);
                String mergeMd5Hex = DigestUtils.md5Hex(mergeFileStream);
                if (!fileMd5.equals(mergeMd5Hex)) {
                    throw new XcException("合并文件校验不通过");
                }
            } catch (IOException e) {
                throw new XcException("合并文件校验出错");
            }

            //拿到合并文件在minIO的存储路径
            String mergeFilePath = getFilePathByMd5(fileMd5, extension);
            //将合并后的文件上传到文件系统
            addMediaFilesToMinIO(tempMergeFile.getAbsolutePath(), bucket_videos, mergeFilePath);

            //将文件信息保存入库
            uploadFileParamsDto.setFileSize(tempMergeFile.length());//合并文件的大小
            addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_videos, mergeFilePath);


            return RestResponse.success(true);
        }finally {
            //删除临时分块文件
            if (files != null) {
                for (File file : files) {
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
            //删除合并的临时文件
            if (tempMergeFile != null) {
                tempMergeFile.delete();
            }
        }
    }

    @Override
    public MediaFiles getFileById(String id) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(id);
        if (mediaFiles == null) {
            throw new XcException("文件不存在");
        }
        String url = mediaFiles.getUrl();
        if (StringUtils.isEmpty(url)){
            throw new XcException("文件还没有处理，请稍后预览");
        }
        return mediaFiles;
    }

    /**
     * 得到合并文件在minIO的存储路径
     * @param fileMd5
     * @param fileExt
     * @return
     */
    private String getFilePathByMd5(String fileMd5,String fileExt){
        return fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
    }

    /**
     * 下载分块
     * @param fileMd5
     * @param chunkTotal 分块文件总数
     * @return
     */
    private File[] checkChunkStatus(String fileMd5, int chunkTotal){
        //得到分块文件所在目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //创建一个分块文件数组用来存放分块文件
        File[] chunkFiles = new File[chunkTotal];
        //开始下载
        for (int i = 0; i < chunkTotal; i++) {
            //分块文件的路径
            String path = chunkFileFolderPath + i;
            //每一个分块文件
            File chunkFile = null;
            try {
                //创建一个临时目录用来存放分块文件
                chunkFile = File.createTempFile("chunk", null);
            } catch (IOException e) {
                throw new XcException("创建分块临时文件出错" + e.getMessage());
            }
            downloadFileFromMinIO(chunkFile, bucket_videos, path);
            chunkFiles[i] = chunkFile;
        }
        return chunkFiles;
    }

    /**
     * 下载分块文件
     * @param file
     * @param bucket
     * @param objectName
     * @return
     */
    public File downloadFileFromMinIO(File file, String bucket, String objectName){
        GetObjectArgs build = GetObjectArgs.builder().bucket(bucket).object(objectName).build();
        try (
                InputStream inputStream = minioClient.getObject(build);
                FileOutputStream fileOutputStream = new FileOutputStream(file)
        ){
            IOUtils.copy(inputStream, fileOutputStream);
            return file;
        } catch (Exception e) {
            throw new XcException("查询分块文件出错" + e.getMessage());
        }
    }

    /**
     * 得到分块文件的目录
     * @param fileMd5
     * @return
     */
    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
    }

    /**
     * 将合并后的文件上传到文件系统
     * @param filePath
     * @param bucket
     * @param objectName
     */
    public void addMediaFilesToMinIO(String filePath, String bucket, String objectName){
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .filename(filePath)
                    .build();
            //上传到minIO
            minioClient.uploadObject(uploadObjectArgs);
        } catch (Exception e) {
            log.error("上传文件到文件系统出错:{}", e.getMessage());
            throw new XcException("上传文件到文件系统出错");
        }
    }

    /**
     * 将文件上传到分布式文件系统
     * @param bytes
     * @param bucket
     * @param objectName
     */
    private void addMediaFilesToMinIO(byte[] bytes, String bucket, String objectName){
        //资源的媒体类型
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//默认未知二进制流

        if (objectName.indexOf(".") >= 0) {
            //取objectName中的扩展名
            String extension = objectName.substring(objectName.lastIndexOf("."));
            ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
            if (extensionMatch != null) {
                contentType = extensionMatch.getMimeType();
            }
        }

        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
                    .contentType(contentType)
                    .build();
            //上传到minIO
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            log.error("上传文件到文件系统出错:{}", e.getMessage());
            throw new XcException("上传文件到文件系统出错");
        }

    }

    /**
     * 根据日期拼接目录
     * @param date
     * @param year
     * @param month
     * @param day
     * @return
     */
    private String getFileFolder(Date date, boolean year, boolean month, boolean day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //获取当前日期字符串
        String dateString = sdf.format(new Date());
        //取出年、月、日
        String[] dateStringArray = dateString.split("-");
        StringBuffer folderString = new StringBuffer();
        if (year) {
            folderString.append(dateStringArray[0]);
            folderString.append("/");
        }
        if (month) {
            folderString.append(dateStringArray[1]);
            folderString.append("/");
        }
        if (day) {
            folderString.append(dateStringArray[2]);
            folderString.append("/");
        }
        return folderString.toString();
    }

    /**
     * 根据扩展名拿匹配的媒体类型
     * @param extension
     * @return
     */
    private String getMimeTypeByExtension(String extension){
        //资源的媒体类型
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//默认未知二进制流
        if (StringUtils.isNotEmpty(extension)) {
            ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
            if (extensionMatch != null) {
                contentType = extensionMatch.getMimeType();
            }
        }
        return contentType;
    }
}
