package com.xc.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xc.base.model.PageParams;
import com.xc.base.model.PageResult;
import com.xc.content.mapper.CourseBaseMapper;
import com.xc.content.mapper.CourseCategoryMapper;
import com.xc.content.mapper.CourseMarketMapper;
import com.xc.content.model.dto.AddCourseDto;
import com.xc.content.model.dto.CourseBaseInfoDto;
import com.xc.content.model.dto.QueryCourseParamsDto;
import com.xc.content.model.po.CourseBase;
import com.xc.content.model.po.CourseCategory;
import com.xc.content.model.po.CourseMarket;
import com.xc.content.service.CourseBaseService;
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
        //合法性校验
        if (StringUtils.isBlank(dto.getName())) {
            throw new RuntimeException("课程名称为空");
        }

        if (StringUtils.isBlank(dto.getMt())) {
            throw new RuntimeException("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getSt())) {
            throw new RuntimeException("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getGrade())) {
            throw new RuntimeException("课程等级为空");
        }

        if (StringUtils.isBlank(dto.getTeachmode())) {
            throw new RuntimeException("教育模式为空");
        }

        if (StringUtils.isBlank(dto.getUsers())) {
            throw new RuntimeException("适应人群为空");
        }

        if (StringUtils.isBlank(dto.getCharge())) {
            throw new RuntimeException("收费规则为空");
        }

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

        //收费规则
        String charge = dto.getCharge();
        //收费课程必须填写价格且大于0元
        if (charge.equals("201001")){
            Float price = dto.getPrice();
            if (price == null || price <= 0) {
                throw new RuntimeException("课程设置了收费价格不能为空且必须大于0");
            }
        }

        //向数据库插入营销信息
        int insertMarket = courseMarketMapper.insert(courseMarket);

        if (insertBase <= 0 || insertMarket <= 0) {
            throw new RuntimeException("新增课程基本信息失败");
        }

        //添加成功，返回添加的课程信息
        return getCourseBaseInfo(courseId);
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
            courseBaseInfoDto.setMt(mtCategory.getName());
        }
        if (stCategory != null) {
            courseBaseInfoDto.setSt(stCategory.getName());
        }

        return courseBaseInfoDto;
    }
}




