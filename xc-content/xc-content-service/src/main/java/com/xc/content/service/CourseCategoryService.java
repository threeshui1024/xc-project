package com.xc.content.service;

import com.xc.content.model.dto.CourseCategoryTreeDto;
import com.xc.content.model.po.CourseCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author DELL
* @description 针对表【course_category(课程分类)】的数据库操作Service
* @createDate 2023-02-14 23:01:03
*/
public interface CourseCategoryService extends IService<CourseCategory> {

    /**
     * 查询课程分类
     */
    List<CourseCategoryTreeDto> queryTreeNodes(String id);

}
