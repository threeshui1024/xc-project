package com.xc.content.mapper;

import com.xc.content.model.po.CourseTeacher;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author DELL
* @description 针对表【course_teacher(课程-教师关系表)】的数据库操作Mapper
* @createDate 2023-02-08 22:13:08
* @Entity com.xc.model.po.CourseTeacher
*/
@Mapper
public interface CourseTeacherMapper extends BaseMapper<CourseTeacher> {

}




