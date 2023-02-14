package com.xc.content.api;

import com.xc.content.model.dto.CourseCategoryTreeDto;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "课程查询相关接口", tags = "课程查询")
@RestController
public class CourseCategoryController {

    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes(){
        return null;
    }
}
