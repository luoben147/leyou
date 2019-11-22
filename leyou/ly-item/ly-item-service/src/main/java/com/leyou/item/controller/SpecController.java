package com.leyou.item.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.ISpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/spec")
public class SpecController {

    @Autowired
    private ISpecificationService specService;

    /**
     * 根据分类id查询参数组
     * @param cid
     * @return
     */
    @GetMapping("/groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroupByCid(@PathVariable("cid") Long cid){
        List<SpecGroup> specGroups = specService.querySpecGroupsByCid(cid);
        if(CollectionUtils.isEmpty(specGroups)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(specGroups);
    }


    /**
     * 新增参数分组
     * @param specGroup
     * @return
     *  //http://api.leyou.com/api/item/spec/group
     */
    @PostMapping("/group")
     public ResponseEntity<Void> saveSpecGroup(@RequestBody SpecGroup specGroup){
         specService.saveSpecGroup(specGroup);
         return new ResponseEntity<>(HttpStatus.CREATED);
     }

    /**
     * 修改参数分组信息
     * @param specGroup
     * @return
     */
    @PutMapping("/group")
    public ResponseEntity<Void> updateSpecGroup(@RequestBody SpecGroup specGroup){
        specService.updateSpecGroup(specGroup);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //http://api.leyou.com/api/item/spec/group/11
    @DeleteMapping("/group/{gid}")
    public ResponseEntity<Void> deleteSpecGroup(@PathVariable("gid") Long gid){
        specService.deleteSpecGroup(gid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 根据条件查询规格参数
     * @param gid  规格组id
     * @param cid 商品分类id
     * @param generic 是否通用字段
     * @param searching 是否用于搜索过滤
     * @return
     */
    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> querySpecParam(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "generic",required = false) Boolean generic,
            @RequestParam(value = "searching",required = false) Boolean searching
     ){
        List<SpecParam> specParam = specService.querySpecParam(gid,cid,generic,searching);
        if(CollectionUtils.isEmpty(specParam)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(specParam);
    }

    /**
     * 新增规格参数
     * http://api.leyou.com/api/item/spec/param
     * {"cid":76,"groupId":1,"segments":"","numeric":false,"searching":false,"generic":false,"name":"测试参数1"}
     * @return
     */
    @PostMapping("/param")
    public ResponseEntity<Void> saveSpecParam(@RequestBody SpecParam specParam){
        specService.saveSpecParam(specParam);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 修改规格参数中某一分组的某个规格参数
     *
     * @param specParam
     * @return
     */
    @PutMapping("param")
    public ResponseEntity<Void> updateSpecParam(@RequestBody SpecParam specParam) {
        specService.updateSpecParam(specParam);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("param/{paramId}")
    public ResponseEntity<Void> deleteSpecParam(@PathVariable("paramId") Long paramId) {
        specService.deleteSpecParam(paramId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 根据cid查询参数组及其组下的参数
     * @param cid
     * @return
     */
    @GetMapping("/group/param/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsWithParam(@PathVariable("cid")Long cid){
        List<SpecGroup> groups= specService.queryGroupsWithParam(cid);
        if(CollectionUtils.isEmpty(groups)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }

}
