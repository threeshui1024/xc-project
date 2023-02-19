package com.xc.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xc.base.execption.XcException;
import com.xc.content.mapper.TeachplanMapper;
import com.xc.content.mapper.TeachplanMediaMapper;
import com.xc.content.model.dto.SaveTeachplanDto;
import com.xc.content.model.dto.TeachplanDto;
import com.xc.content.model.po.Teachplan;
import com.xc.content.model.po.TeachplanMedia;
import com.xc.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author DELL
* @description 针对表【teachplan(课程计划)】的数据库操作Service实现
* @createDate 2023-02-08 22:13:08
*/
@Service
public class TeachplanServiceImpl extends ServiceImpl<TeachplanMapper, Teachplan>
    implements TeachplanService {

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Autowired
    private TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDto> selectTreeNodes(long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Override
    public void saveTeachplan(SaveTeachplanDto dto) {
        Teachplan teachplan = teachplanMapper.selectById(dto.getId());
        if (teachplan == null) {//新增
            teachplan = new Teachplan();
            BeanUtils.copyProperties(dto, teachplan);
            //找到同级别课程计划的数量
            int count = getTeachplanCount(dto.getCourseId(), dto.getParentid());
            //设置新课程计划的值
            teachplan.setOrderby(count + 1);
            teachplanMapper.insert(teachplan);
        }else {//修改
            BeanUtils.copyProperties(dto, teachplan);
            teachplanMapper.updateById(teachplan);
        }
    }

    @Transactional
    @Override
    public void deleteTeachplan(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        if (teachplan == null) {
            throw new XcException("无法找到该章节");
        }
        //删除第一级别的章时要求章下边没有小节方可删除
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid, id);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new XcException("存在子章节，无法删除该章节");
        }

        //删除第二级别的小节的同时需要将其它关联的视频信息也删除
        LambdaQueryWrapper<TeachplanMedia> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeachplanMedia::getTeachplanId, id);
        Integer row = teachplanMediaMapper.selectCount(wrapper);
        if (row > 0) {
            teachplanMediaMapper.delete(wrapper);
        }
        teachplanMapper.deleteById(id);
    }

    @Override
    public void moveup(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        Integer orderby = teachplan.getOrderby();
        if (orderby == 1) {
            throw new XcException("已经是第一个了，无法继续上移");
        }
        //查询上移的前一个
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid, teachplan.getParentid());
        queryWrapper.eq(Teachplan::getOrderby, orderby - 1);
        Teachplan preTeachplan = teachplanMapper.selectOne(queryWrapper);
        //调整orderBy字段
        preTeachplan.setOrderby(orderby);
        teachplan.setOrderby(orderby - 1);
        teachplanMapper.updateById(preTeachplan);
        teachplanMapper.updateById(teachplan);
    }

    @Override
    public void movedown(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        Integer orderby = teachplan.getOrderby();
        Long parentid = teachplan.getParentid();
        //查询这一章节共有几小节
        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teachplan::getParentid, parentid);
        Integer count = teachplanMapper.selectCount(wrapper);
        if (orderby.equals(count)) {
            throw new XcException("已经是最后一个了，无法继续下移");
        }
        //查询下移的后一个
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid, parentid);
        queryWrapper.eq(Teachplan::getOrderby, orderby + 1);
        Teachplan nextTeachplan = teachplanMapper.selectOne(queryWrapper);
        //调整orderBy字段
        nextTeachplan.setOrderby(orderby);
        teachplan.setOrderby(orderby + 1);
        teachplanMapper.updateById(nextTeachplan);
        teachplanMapper.updateById(teachplan);
    }

    /**
     * 获取最新的排序号
     * @param courseId 课程id
     * @param parentId 父课程计划id
     * @return 最新排序号
     */
    private int getTeachplanCount(long courseId,long parentId){
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        queryWrapper.eq(Teachplan::getParentid, parentId);
        return teachplanMapper.selectCount(queryWrapper);
    }
}




