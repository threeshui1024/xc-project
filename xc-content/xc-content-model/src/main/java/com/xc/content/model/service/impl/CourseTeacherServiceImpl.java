package com.xc.content.model.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xc.content.model.service.CourseTeacherService;
import com.xc.content.model.po.CourseTeacher;
import com.xc.content.model.mapper.CourseTeacherMapper;
import org.springframework.stereotype.Service;

/**
* @author DELL
* @description 针对表【course_teacher(课程-教师关系表)】的数据库操作Service实现
* @createDate 2023-02-08 22:13:08
*/
@Service
public class CourseTeacherServiceImpl extends ServiceImpl<CourseTeacherMapper, CourseTeacher>
    implements CourseTeacherService {

}




