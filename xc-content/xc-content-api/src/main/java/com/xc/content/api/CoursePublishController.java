package com.xc.content.api;

import com.xc.content.model.dto.CoursePreviewDto;
import com.xc.content.service.CoursePublishService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Mr.M
 * @version 1.0
 * @description 课程预览，发布
 * @date 2022/9/16 14:48
 */
@Controller
public class CoursePublishController {

    @Autowired
    CoursePublishService coursePublishService;

    /**
     * 获取模板引擎需要的模型数据
     * @param courseId 课程id
     * @return
     */
    @GetMapping("/coursepreview1/{courseId}")
    public ModelAndView preview(@PathVariable("courseId") Long courseId) {
        //获取课程预览信息
        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(courseId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("model", coursePreviewInfo);
        modelAndView.setViewName("course_template");
        return modelAndView;
    }

    /**
     * 提交审核接口
     * @param courseId
     */
    @ResponseBody
    @PostMapping("/courseaudit/commit/{courseId}")
    public void commitAudit(@PathVariable("courseId") Long courseId){
        coursePublishService.commitAudit(22L, courseId);
    }

    //课程审核接口 TODO

    /**
     * 课程发布
     * @param courseId
     */
    @ApiOperation("课程发布")
    @ResponseBody
    @PostMapping ("/coursepublish/{courseId}")
    public void coursepublish(@PathVariable("courseId") Long courseId){
        coursePublishService.publish(22L, courseId);
    }

}