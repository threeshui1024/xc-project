package com.xc.content.mapper;

import com.xc.content.model.dto.CourseCategoryTreeDto;
import com.xc.content.model.po.CourseCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author DELL
* @description 针对表【course_category(课程分类)】的数据库操作Mapper
* @createDate 2023-02-14 23:01:03
* @Entity com.xc.content.model.po.CourseCategory
*/
@Mapper
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {
    /**
     * 查询课程分类
     */
    List<CourseCategoryTreeDto> selectTreeNodes(String id);

}




