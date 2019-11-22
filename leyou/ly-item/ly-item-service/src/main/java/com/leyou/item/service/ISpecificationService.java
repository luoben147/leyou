package com.leyou.item.service;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;

import java.util.List;

public interface ISpecificationService {
    List<SpecGroup> querySpecGroupsByCid(Long cid);

    List<SpecParam> querySpecParam(Long gid, Long cid, Boolean generic,Boolean searching);

    void saveSpecGroup(SpecGroup specGroup);

    void updateSpecGroup(SpecGroup specGroup);

    void deleteSpecGroup(Long gid);

    void saveSpecParam(SpecParam specParam);

    void updateSpecParam(SpecParam specParam);

    void deleteSpecParam(Long paramId);

    List<SpecGroup> queryGroupsWithParam(Long cid);
}
