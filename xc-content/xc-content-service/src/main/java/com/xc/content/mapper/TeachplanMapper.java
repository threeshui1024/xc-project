package com.xc.content.mapper;

import com.xc.content.model.dto.TeachplanDto;
import com.xc.content.model.po.Teachplan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author DELL
* @description 针对表【teachplan(课程计划)】的数据库操作Mapper
* @createDate 2023-02-08 22:13:08
* @Entity com.xc.model.po.Teachplan
*/
@Mapper
public interface TeachplanMapper extends BaseMapper<Teachplan> {

    /**
     * 查询某课程的课程计划，组成树型结构
     * @param courseId
     * @return
     */
    public List<TeachplanDto> selectTreeNodes(long courseId);

}




