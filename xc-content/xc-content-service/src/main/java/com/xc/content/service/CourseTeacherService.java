package com.xc.content.service;

import com.xc.content.model.po.CourseTeacher;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author DELL
* @description 针对表【course_teacher(课程-教师关系表)】的数据库操作Service
* @createDate 2023-02-08 22:13:08
*/
public interface CourseTeacherService extends IService<CourseTeacher> {

    /**
     * 查询课程教师
     * @param courseId 课程id
     * @return
     */
    List<CourseTeacher> selectCourseTeacher(Long courseId);

    /**
     * 添加/修改教师信息
     * @param courseTeacher
     */
    void saveCourseTeacher(CourseTeacher courseTeacher);

    /**
     * 删除教师信息
     * @param courseId 课程id
     * @param id 教师id
     */
    void deleteCourseTeacher(Long courseId, Long id);
}
