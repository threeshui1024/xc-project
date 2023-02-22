package com.xc.media.service;

import com.xc.base.model.PageParams;
import com.xc.base.model.PageResult;
import com.xc.base.model.RestResponse;
import com.xc.media.model.dto.QueryMediaParamsDto;
import com.xc.media.model.dto.UploadFileParamsDto;
import com.xc.media.model.dto.UploadFileResultDto;
import com.xc.media.model.po.MediaFiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理业务类
 * @date 2022/9/10 8:55
 */
public interface MediaFileService {

    /**
     * @param pageParams          分页参数
     * @param queryMediaParamsDto 查询条件
     * @return com.xc.base.model.PageResult<com.xc.media.model.po.MediaFiles>
     * @description 媒资文件查询方法
     * @author Mr.M
     * @date 2022/9/10 8:57
     */
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    /**
     * 上传文件的通用接口，可以上传图片或其它文件
     *
     * @param companyId           机构id
     * @param uploadFileParamsDto 文件信息
     * @param bytes               文件字节数组
     * @param folder              桶下边的子目录
     * @param objectName          对象名称
     * @return
     */
    UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, byte[] bytes, String folder, String objectName);

    /**
     * 保存文件信息到数据库
     * @param companyId 机构id
     * @param fileMd5 主键
     * @param uploadFileParamsDto 文件信息
     * @param bucket 桶名称
     * @param objectName 文件名称
     * @return
     */
    MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName);

    /**
     * 检查文件是否存在(数据库存在，minIO文件系统存在)
     * @param fileMd5
     * @return
     */
    RestResponse<Boolean> checkfile(String fileMd5);

    /**
     * 检查分块是否存在
     * @param fileMd5
     * @param chunk
     * @return
     */
    RestResponse<Boolean> checkchunk(String fileMd5, int chunk);

    /**
     * 上传分块
     * @param fileMd5
     * @param chunk
     * @param bytes
     * @return
     */
    RestResponse uploadChunk(String fileMd5,int chunk,byte[] bytes);

    /**
     * 合并分块
     * @param companyId
     * @param fileMd5
     * @param chunkTotal
     * @param uploadFileParamsDto
     * @return
     */
    RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto);

    /**
     * 根据id查看文件信息
     * @param id
     * @return
     */
    public MediaFiles getFileById(String id);
}
