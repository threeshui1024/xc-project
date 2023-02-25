package com.xc.content.api;

import com.xc.content.model.dto.BindTeachplanMediaDto;
import com.xc.content.model.dto.SaveTeachplanDto;
import com.xc.content.model.dto.TeachplanDto;
import com.xc.content.model.po.TeachplanMedia;
import com.xc.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程计划编辑接口
 */
@RestController
@Api("课程计划编辑接口")
public class TeachplanController {

    @Autowired
    private TeachplanService teachplanService;

    /**
     * 查询课程计划树形结构
     * @param courseId
     * @return
     */
    @ApiOperation("查询课程计划树形结构")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> selectTreeNodes(@PathVariable Long courseId){
        return teachplanService.selectTreeNodes(courseId);
    }

    /**
     * 课程计划创建或修改
     * @param dto
     */
    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public void saveTeachplan(@RequestBody SaveTeachplanDto dto){
        teachplanService.saveTeachplan(dto);
    }

    /**
     * 根据id删除课程计划
     * @param id
     */
    @DeleteMapping("/teachplan/{id}")
    public void deleteTeachplan(@PathVariable Long id){
        teachplanService.deleteTeachplan(id);
    }

    /**
     * 向上移动
     * @param id
     */
    @PostMapping("/teachplan/moveup/{id}")
    public void moveup(@PathVariable Long id){
        teachplanService.moveup(id);
    }

    /**
     * 向下移动
     * @param id
     */
    @PostMapping("/teachplan/movedown/{id}")
    public void movedown(@PathVariable Long id){
        teachplanService.movedown(id);
    }

    /**
     * 课程计划和媒资信息绑定
     * @param bindTeachplanMediaDto
     */
    @ApiOperation(value = "课程计划和媒资信息绑定")
    @PostMapping("/teachplan/association/media")
    public void associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto){
        teachplanService.associationMedia(bindTeachplanMediaDto);
    }

    /**
     * 删除教学计划与媒资之间的绑定关系
     * @param teachPlanId 教学计划Id
     * @param mediaId 媒资文件Id
     */
    @DeleteMapping("/teachplan/association/media/{teachPlanId}/{mediaId}")
    public void deleteAssociationMedia(@PathVariable Long teachPlanId, @PathVariable String mediaId){
        teachplanService.deleteAssociationMedia(teachPlanId, mediaId);
    }
}
