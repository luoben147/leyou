package com.leyou.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.IBrandService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandServiceImpl implements IBrandService {

    @Autowired
    private BrandMapper brandMapper;

    /**
     * 根据查询条件分页并排序查询品牌信息
     *
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    @Override
    public PageResult<Brand> queryBrandsByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        //初始化Example
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();

        //根据 name 模糊查询，或者根据首字母查询
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("name", "%" + key + "%").orEqualTo("letter", key);
        }

        //添加分页条件
        PageHelper.startPage(page, rows);

        //添加排序条件
        if (StringUtils.isNotBlank(sortBy)) {
            example.setOrderByClause(sortBy + " " + (desc ? "desc" : "asc"));
        }

        List<Brand> brands = brandMapper.selectByExample(example);
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);

        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }

    @Transactional
    @Override
    public void saveBrand(Brand brand, List<Long> cids) {
        //新增Brand
        brandMapper.insertSelective(brand);
        //新增中间表
        cids.forEach(cid -> {
            brandMapper.insertCategoryBrand(cid, brand.getId());
        });

    }

    @Transactional
    @Override
    public void updateBrand(Brand brand, List<Long> cids) {
        // 修改品牌信息
        brandMapper.updateByPrimaryKey(brand);
        //删除原来的中间表数据
        brandMapper.deleteCidByBid(brand.getId());
        //维护品牌和分类中间表
        for (Long cid : cids) {
            brandMapper.insertCategoryBrand(cid,brand.getId());
        }
    }

    /**
     * 品牌删除
     * @param bid
     */
    @Transactional
    @Override
    public void deleteBrandByBid(Long bid) {
        //删除品牌信息
        brandMapper.deleteByPrimaryKey(bid);
        //维护中间表
        brandMapper.deleteCidByBid(bid);
    }

    /**
     * 根据分类查询品牌
     * @param cid
     * @return
     */
    @Override
    public List<Brand> queryBrandsByCid(Long cid) {
        return  brandMapper.selectBrandsByCid(cid);
    }

    /**
     * 根据品牌id 查询品牌信息
     * @param id
     * @return
     */
    @Override
    public Brand queryBrandById(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }
}
