package com.xc.media.api;

import com.xc.base.model.RestResponse;
import com.xc.media.model.dto.UploadFileParamsDto;
import com.xc.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Mr.M
 * @version 1.0
 * @description 上传文件管理接口
 * @date 2022/9/6 11:29
 */
@Api(value = "上传文件管理接口", tags = "上传文件管理接口")
@RestController
public class BigFilesController {

    @Autowired
    MediaFileService mediaFileService;

    /**
     * 检查文件是否存在(数据库存在，minIO文件系统存在)
     * @param fileMd5
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "文件上传前检查文件")
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkfile(@RequestParam("fileMd5") String fileMd5) throws Exception {
        return mediaFileService.checkfile(fileMd5);
    }

    /**
     * 检查分块是否存在
     * @param fileMd5
     * @param chunk
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "分块文件上传前的检测")
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkchunk(@RequestParam("fileMd5") String fileMd5,
                                            @RequestParam("chunk") int chunk) throws Exception {
        return mediaFileService.checkchunk(fileMd5, chunk);
    }

    /**
     * 上传分块
     * @param file
     * @param fileMd5
     * @param chunk
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "上传分块文件")
    @PostMapping("/upload/uploadchunk")
    public RestResponse uploadchunk(@RequestParam("file") MultipartFile file,
                                    @RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("chunk") int chunk) throws Exception {
        return mediaFileService.uploadChunk(fileMd5, chunk, file.getBytes());

    }

    /**
     * 合并分块
     * @param fileMd5
     * @param fileName
     * @param chunkTotal
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "合并文件")
    @PostMapping("/upload/mergechunks")
    public RestResponse mergechunks(@RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("fileName") String fileName,
                                    @RequestParam("chunkTotal") int chunkTotal) throws Exception {
        Long companyId = 22L;
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFilename(fileName);
        uploadFileParamsDto.setFileType("001002");
        uploadFileParamsDto.setTags("课程视频");
        return mediaFileService.mergechunks(companyId, fileMd5, chunkTotal, uploadFileParamsDto);
    }


}
