package com.xc.content.model.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @author xioaming
 * @version 1.0
 * @description 课程查询参数Dto：数据传输对象（DTO）(Data Transfer Object)，用于接口层和业务层之间传输数据
 * @date 2023/1/16 15:39
 */
@Data
@ToString
public class QueryCourseParamsDto {
    //审核状态
    private String auditStatus;
    //课程名称
    private String courseName;
    //发布状态
    private String publishStatus;
}
