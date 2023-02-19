package com.xc.content;


import com.xc.base.model.PageParams;
import com.xc.base.model.PageResult;
import com.xc.content.mapper.CourseBaseMapper;
import com.xc.content.model.dto.QueryCourseParamsDto;
import com.xc.content.model.po.CourseBase;
import com.xc.content.service.CourseBaseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class XcContentServiceApplicationTest {

    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CourseBaseService courseBaseService;


    @Test
    void testCourseBaseMapper(){
        CourseBase courseBase = courseBaseMapper.selectById(120);
        Assertions.assertNotNull(courseBase);
    }

    @Test
    void testCourseBaseService(){

        PageResult<CourseBase> page = courseBaseService.queryCourseBaseList(new PageParams(), new QueryCourseParamsDto());
        System.out.println(page);
    }
}
