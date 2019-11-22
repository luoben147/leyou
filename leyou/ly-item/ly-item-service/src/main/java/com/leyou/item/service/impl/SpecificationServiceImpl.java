package com.leyou.item.service.impl;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.ISpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpecificationServiceImpl implements ISpecificationService {

    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    private SpecParamMapper specParamMapper;
    /**
     * 根据分类id查询参数组
     * @param cid
     * @return
     */
    @Transactional
    @Override
    public List<SpecGroup> querySpecGroupsByCid(Long cid) {
        SpecGroup specGroup=new SpecGroup();
        specGroup.setCid(cid);
        return specGroupMapper.select(specGroup);
    }

    /**
     * 新增规格参数分组
     * @param specGroup
     */
    @Transactional
    @Override
    public void saveSpecGroup(SpecGroup specGroup) {
        specGroupMapper.insertSelective(specGroup);
    }

    /**
     * 修改规格参数分组
     * @param specGroup
     */
    @Transactional
    @Override
    public void updateSpecGroup(SpecGroup specGroup) {
        specGroupMapper.updateByPrimaryKeySelective(specGroup);
    }

    /**
     * 删除规格参数分组
     * @param id
     */
    @Override
    @Transactional
    public void deleteSpecGroup(Long id) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(id);
        //删除改组的所有参数
        specParamMapper.delete(specParam);
        //删除该组
        specGroupMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据条件查询规格参数
     * @param gid
     * @return
     */
    @Override
    public List<SpecParam> querySpecParam(Long gid,Long cid, Boolean generic,Boolean searching) {
        SpecParam specParam=new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setGeneric(generic);
        specParam.setSearching(searching);
        return specParamMapper.select(specParam);
    }

    /**
     * 新增规格参数
     * @param specParam
     */
    @Transactional
    @Override
    public void saveSpecParam(SpecParam specParam) {
        specParamMapper.insertSelective(specParam);
    }

    /**
     *  修改规格参数
     * @param specParam
     */
    @Transactional
    @Override
    public void updateSpecParam(SpecParam specParam) {
        specParamMapper.updateByPrimaryKey(specParam);
    }

    /**
     * 删除一条规格参数
     * @param paramId
     */
    @Transactional
    @Override
    public void deleteSpecParam(Long paramId) {
        specParamMapper.deleteByPrimaryKey(paramId);
    }

    /**
     * 根据cid查询参数组及其组下的参数
     * @param cid
     * @return
     */
    @Override
    public List<SpecGroup> queryGroupsWithParam(Long cid) {
        List<SpecGroup> groups = querySpecGroupsByCid(cid);
        groups.forEach(group ->{
            List<SpecParam> params = querySpecParam(group.getId(), null, null, null);
            group.setSpecParams(params);
        });

        return groups;
    }


}
