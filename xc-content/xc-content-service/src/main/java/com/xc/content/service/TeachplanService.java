package com.xc.content.service;

import com.xc.content.model.dto.SaveTeachplanDto;
import com.xc.content.model.dto.TeachplanDto;
import com.xc.content.model.po.Teachplan;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author DELL
* @description 针对表【teachplan(课程计划)】的数据库操作Service
* @createDate 2023-02-08 22:13:08
*/
public interface TeachplanService extends IService<Teachplan> {

    /**
     * 查询课程计划树型结构
     * @param courseId
     * @return
     */
    List<TeachplanDto> selectTreeNodes(long courseId);

    /**
     * 课程计划创建或修改
     * @param dto
     */
    void saveTeachplan(SaveTeachplanDto dto);
}
