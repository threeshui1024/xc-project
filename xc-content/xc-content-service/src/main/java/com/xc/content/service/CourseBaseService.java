package com.xc.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xc.base.model.PageParams;
import com.xc.base.model.PageResult;
import com.xc.content.model.dto.AddCourseDto;
import com.xc.content.model.dto.CourseBaseInfoDto;
import com.xc.content.model.dto.EditCourseDto;
import com.xc.content.model.dto.QueryCourseParamsDto;
import com.xc.content.model.po.CourseBase;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

/**
* @author DELL
* @description 针对表【course_base(课程基本信息)】的数据库操作Service
* @createDate 2023-02-08 22:07:21
*/
public interface CourseBaseService extends IService<CourseBase> {

    /**
     * 课程查询接口
     * @param pageParams 分页信息
     * @param queryCourseParamsDto 参数
     * @return
     */
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    /**
     * 新增课程
     * @param companyId 教学机构id
     * @param addCourseDto 课程基本信息
     * @return
     */
    CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

    /**
     * 查询刚刚插入的课程的基本信息和营销信息
     * @param courseId 课程id
     * @return
     */
    CourseBaseInfoDto getCourseBaseInfo(long courseId);

    /**
     * 修改课程基本信息和营销信息
     * @param companyId 机构id
     * @param dto 课程信息
     * @return
     */
    CourseBaseInfoDto modifyCourseBase(Long companyId, EditCourseDto dto);

}
