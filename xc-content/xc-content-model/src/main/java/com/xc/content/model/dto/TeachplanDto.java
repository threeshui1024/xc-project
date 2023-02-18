package com.xc.content.model.dto;

import com.xc.content.model.po.Teachplan;
import com.xc.content.model.po.TeachplanMedia;
import lombok.Data;

import java.util.List;

@Data
public class TeachplanDto extends Teachplan {

    /**
     * 课程计划关联的媒资信息
     */
    TeachplanMedia teachplanMedia;

    /**
     * 子节点
     */
    List<TeachplanDto> teachPlanTreeNodes;

}
