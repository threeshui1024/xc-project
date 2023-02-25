package com.xc.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xc.content.model.po.CoursePublish;
import org.apache.ibatis.annotations.Mapper;

/**
* @author DELL
* @description 针对表【course_publish(课程发布)】的数据库操作Mapper
* @createDate 2023-02-25 22:25:31
* @Entity com.xc.content.model.po.CoursePublish
*/
@Mapper
public interface CoursePublishMapper extends BaseMapper<CoursePublish> {

}




