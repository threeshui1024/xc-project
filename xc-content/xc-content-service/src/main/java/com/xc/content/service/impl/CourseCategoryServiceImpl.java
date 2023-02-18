package com.xc.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xc.content.mapper.CourseCategoryMapper;
import com.xc.content.model.dto.CourseCategoryTreeDto;
import com.xc.content.model.po.CourseCategory;
import com.xc.content.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
* @author DELL
* @description 针对表【course_category(课程分类)】的数据库操作Service实现
* @createDate 2023-02-14 23:01:03
*/
@Service
public class CourseCategoryServiceImpl extends ServiceImpl<CourseCategoryMapper, CourseCategory>
    implements CourseCategoryService {

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);
        //定义一个List作为最终返回的数据
        ArrayList<CourseCategoryTreeDto> returnList = new ArrayList<>();
        //为了方便搜索子节点的父节点，定义一个map
        HashMap<String, CourseCategoryTreeDto> hashMap = new HashMap<>();

        //将数据封装到list中，只包括根节点的直接下属节点
        courseCategoryTreeDtos.stream().forEach(item -> {
            hashMap.put(item.getId(), item);
            if (item.getParentid().equals(id)){
                returnList.add(item);
            }
            //找到该节点的父节点
            String parentid = item.getParentid();
            //找到该节点的父节点对象
            CourseCategoryTreeDto parentNode = hashMap.get(parentid);
            if (parentNode != null) {
                List childrenTreeNodes = parentNode.getChildrenTreeNodes();
                if (childrenTreeNodes == null) {
                    parentNode.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                }
                //找到了子节点，放到父节点的childrenTreeNodes属性中
                parentNode.getChildrenTreeNodes().add(item);
            }
        });
        return returnList;
    }
}




