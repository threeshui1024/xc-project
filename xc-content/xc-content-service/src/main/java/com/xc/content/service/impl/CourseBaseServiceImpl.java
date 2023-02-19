package com.xc.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xc.base.execption.XcException;
import com.xc.base.model.PageParams;
import com.xc.base.model.PageResult;
import com.xc.content.mapper.*;
import com.xc.content.model.dto.AddCourseDto;
import com.xc.content.model.dto.CourseBaseInfoDto;
import com.xc.content.model.dto.EditCourseDto;
import com.xc.content.model.dto.QueryCourseParamsDto;
import com.xc.content.model.po.*;
import com.xc.content.service.CourseBaseService;
import com.xc.content.service.CourseMarketService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
* @author DELL
* @description 针对表【course_base(课程基本信息)】的数据库操作Service实现
* @createDate 2023-02-08 22:07:21
*/
@Service
public class CourseBaseServiceImpl extends ServiceImpl<CourseBaseMapper, CourseBase>
    implements CourseBaseService {

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Autowired
    private CourseMarketMapper courseMarketMapper;

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    @Autowired
    private CourseMarketService courseMarketService;

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Autowired
    private CourseTeacherMapper courseTeacherMapper;

    @Autowired
    private TeachplanMediaMapper teachplanMediaMapper;

    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto){

        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //组装课程名称
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()), CourseBase::getName, queryCourseParamsDto.getCourseName());
        //组装审核状态
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());
        //组装发布状态
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()), CourseBase::getStatus, queryCourseParamsDto.getPublishStatus());

        //分页对象
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());

        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);
        //获取数据列表
        List<CourseBase> list = pageResult.getRecords();
        //获取总记录数
        long total = pageResult.getTotal();

        return new PageResult<CourseBase>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
    }


    @Override
    @Transactional
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {

        //新增课程基本信息对象
        CourseBase courseBase = new CourseBase();
        //拷贝属性
        BeanUtils.copyProperties(dto, courseBase);
        //设置审核状态
        courseBase.setAuditStatus("202002");
        //设置发布状态
        courseBase.setStatus("203001");
        //机构id
        courseBase.setCompanyId(companyId);
        //添加时间
        courseBase.setCreateDate(LocalDateTime.now());
        //向数据库插入课程基本信息
        int insertBase = courseBaseMapper.insert(courseBase);

        //获取新插入的课程的id
        Long courseId = courseBase.getId();
        //新增课程营销信息对象
        CourseMarket courseMarket = new CourseMarket();
        //拷贝属性
        BeanUtils.copyProperties(dto, courseMarket);
        //将新插入的课程营销信息的id与新插入的课程基本信息的id保持一致
        courseMarket.setId(courseId);

        int row = saveCourseMarket(courseMarket);

        if (insertBase <= 0 || row <= 0) {
            XcException.cast("新增课程基本信息失败");
        }

        //添加成功，返回添加的课程信息
        return getCourseBaseInfo(courseId);
    }

    /**
     * 抽取对营销表的保存
     * @param courseMarket
     * @return
     */
    private int saveCourseMarket(CourseMarket courseMarket) {
        //收费规则
        String charge = courseMarket.getCharge();
        //收费课程必须填写价格且大于0元
        if (charge.equals("201001")){
            Float price = courseMarket.getPrice();
            if (price == null || price.floatValue() <= 0) {
                XcException.cast("课程设置了收费价格不能为空且必须大于0");
            }
        }
        //有则更新，没有则新增
        boolean b = courseMarketService.saveOrUpdate(courseMarket);
        return b ? 1:0;
    }

    /**
     * 查询刚刚插入的课程的基本信息和营销信息
     * @param courseId 课程id
     * @return
     */
    public CourseBaseInfoDto getCourseBaseInfo(long courseId){
        //查询基本信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        //查询营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        //拷贝属性
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);

        //根据课程分类的id查询课程分类的名称
        CourseCategory mtCategory = courseCategoryMapper.selectById(courseBase.getMt());//大分类
        CourseCategory stCategory = courseCategoryMapper.selectById(courseBase.getSt());//小分类

        if (mtCategory != null) {
            courseBaseInfoDto.setMtName(mtCategory.getName());
        }
        if (stCategory != null) {
            courseBaseInfoDto.setStName(stCategory.getName());
        }

        return courseBaseInfoDto;
    }

    @Override
    @Transactional
    public CourseBaseInfoDto modifyCourseBase(Long companyId, EditCourseDto dto) {
        //获取课程id
        Long courseId = dto.getId();
        //根据课程id查询课程信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (!companyId.equals(courseBase.getCompanyId())){
            XcException.cast("只允许修改本机构的课程");
        }
        //将修改信息进行拷贝
        BeanUtils.copyProperties(dto, courseBase);

        //更新
        courseBase.setChangeDate(LocalDateTime.now());//先更新一下修改时间
        courseBaseMapper.updateById(courseBase);

        //营销信息要先查询，有则更新，没有则新增,所以这里使用new的方式
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(dto, courseMarket);
        this.saveCourseMarket(courseMarket);

        return getCourseBaseInfo(courseId);
    }

    @Transactional
    @Override
    public void deleteCourseBase(Long courseId) {
        //删除课程需要删除课程相关的基本信息、营销信息、课程计划、课程教师信息。
        //课程的审核状态为未提交时方可删除
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        String status = courseBase.getAuditStatus();
        if (!status.equals("202002")){
            throw new XcException("只能删除审核状态为未提交的课程");
        }
        //1.删除基本信息
        courseBaseMapper.deleteById(courseId);

        //2.删除营销信息
        courseMarketMapper.deleteById(courseId);

        //3.删除课程计划
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        teachplanMapper.delete(queryWrapper);

        //4.删除课程计划资源表
        LambdaQueryWrapper<TeachplanMedia> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TeachplanMedia::getCourseId, courseId);
        teachplanMediaMapper.delete(lambdaQueryWrapper);

        //5.删除课程教师信息
        LambdaQueryWrapper<CourseTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseTeacher::getCourseId, courseId);
        courseTeacherMapper.delete(wrapper);
    }
}




