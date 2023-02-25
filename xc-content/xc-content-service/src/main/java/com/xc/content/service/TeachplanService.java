package com.xc.content.service;

import com.xc.content.model.dto.BindTeachplanMediaDto;
import com.xc.content.model.dto.SaveTeachplanDto;
import com.xc.content.model.dto.TeachplanDto;
import com.xc.content.model.po.Teachplan;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xc.content.model.po.TeachplanMedia;

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

    /**
     * 根据id删除课程计划
     * @param id
     */
    void deleteTeachplan(Long id);

    /**
     * 向上移动
     * @param id
     */
    void moveup(Long id);

    /**
     * 向下移动
     * @param id
     */
    void movedown(Long id);

    /**
     * 课程计划和媒资信息绑定
     * @param bindTeachplanMediaDto
     */
    TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

    /**
     * @description 删除教学计划与媒资之间的绑定关系
     * @param teachPlanId 教学计划Id
     * @param mediaId 媒资文件Id
     * @return void
     * @author xiaoming
     * @date 2023/2/3 16:20
     */
    public void deleteAssociationMedia(Long teachPlanId, String mediaId);
}
