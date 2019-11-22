package com.leyou.item.service.impl;

import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import com.leyou.item.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 根据父节点查询子节点
     * @param pid
     * @return
     */
    @Override
    public List<Category> queryCategoriesByPid(Long pid) {
        Category record = new Category();
        record.setParentId(pid);
        return categoryMapper.select(record);
    }

    /**
     * 根据brand id查询分类信息
     * @param bid
     * @return
     */
    @Override
    public List<Category> queryByBrandId(Long bid) {
        return categoryMapper.queryByBrandId(bid);
    }

    /**
     * 根据多个分类id 查询对应的名称
     * @return
     */
    @Override
    public List<String> queryNamesByIds(List<Long> cids) {
        List<Category> categories = categoryMapper.selectByIdList(cids);
        return   categories.stream().map(category -> {
            return category.getName();
        }).collect(Collectors.toList());
    }
}
