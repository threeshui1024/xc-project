package com.xc.content.model.dto;

import lombok.Data;

/**
 * 修改课程dto
 */
@Data
public class EditCourseDto extends AddCourseDto{
    /**
     * 课程id
     */
    private Long id;
}
