package com.xc.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xc.base.model.PageParams;
import com.xc.base.model.PageResult;
import com.xc.content.model.dto.QueryCourseParamsDto;
import com.xc.content.model.po.CourseBase;

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

}
