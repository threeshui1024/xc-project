package com.xc.content.model.dto;

import com.xc.content.model.po.CourseCategory;

import java.io.Serializable;
import java.util.List;

public class CourseCategoryTreeDto extends CourseCategory implements Serializable {
    List childrenTreeNodes;
}
