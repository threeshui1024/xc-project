package com.xc.content.api;

import com.xc.content.model.po.CourseTeacher;
import com.xc.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程教师接口
 */
@Api("课程教师接口")
@RestController
public class CourseTeacherController {

    @Autowired
    private CourseTeacherService courseTeacherService;

    /**
     * 查询课程教师
     * @param courseId 课程id
     * @return
     */
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> selectCourseTeacher(@PathVariable Long courseId){
        return courseTeacherService.selectCourseTeacher(courseId);
    }

    /**
     * 添加/修改教师信息
     * @param courseTeacher
     */
    @PostMapping("/courseTeacher")
    public void saveCourseTeacher(@RequestBody @Validated CourseTeacher courseTeacher){
        courseTeacherService.saveCourseTeacher(courseTeacher);
    }

    /**
     * 删除教师信息
     * @param courseId 课程id
     * @param id 教师id
     */
    @DeleteMapping("/courseTeacher/course/{courseId}/{id}")
    public void deleteCourseTeacher(@PathVariable Long courseId, @PathVariable Long id){
        courseTeacherService.deleteCourseTeacher(courseId, id);
    }
}
