package com.xc.content.api;

import com.xc.base.model.PageParams;
import com.xc.base.model.PageResult;
import com.xc.content.model.dto.AddCourseDto;
import com.xc.content.model.dto.CourseBaseInfoDto;
import com.xc.content.model.dto.EditCourseDto;
import com.xc.content.model.dto.QueryCourseParamsDto;
import com.xc.content.model.po.CourseBase;
import com.xc.content.service.CourseBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(value = "课程管理相关的接口", tags = "课程管理")
@RestController
public class CourseBaseInfoController {

    @Autowired
    private CourseBaseService courseBaseService;

    /**
     * 课程查询
     * @param pageParams
     * @param queryCourseParamsDto
     * @return
     */
    @ApiOperation("课程查询")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody QueryCourseParamsDto queryCourseParamsDto){
        return courseBaseService.queryCourseBaseList(pageParams, queryCourseParamsDto);
    }

    /**
     * 新增课程
     * @param addCourseDto
     * @return
     */
    @ApiOperation("新增课程")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated AddCourseDto addCourseDto){
        //机构id，由于认证系统没有上线暂时硬编码
        Long companyId = 22L;
        return courseBaseService.createCourseBase(companyId, addCourseDto);
    }

    /**
     * 根据课程id查询课程基础信息
     * @param courseId 课程id
     * @return
     */
    @ApiOperation("根据课程id查询课程基础信息和营销信息")
    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId){
        return courseBaseService.getCourseBaseInfo(courseId);
    }

    /**
     * 修改课程基本信息和营销信息
     * @param dto
     * @return
     */
    @ApiOperation("修改课程基本信息和营销信息")
    @PutMapping("/course")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody @Validated EditCourseDto dto){
        return courseBaseService.modifyCourseBase(22L, dto);
    }

    /**
     * 删除课程信息
     * @param courseId
     */
    @DeleteMapping("/course/{courseId}")
    public void deleteCourseBase(@PathVariable Long courseId){
        courseBaseService.deleteCourseBase(courseId);
    }


}
