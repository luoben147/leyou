package com.leyou.item.api;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/spec")
public interface SpecApi {

    /**
     * 根据条件查询规格参数
     *
     * @param gid       规格组id
     * @param cid       商品分类id
     * @param generic   是否通用字段
     * @param searching 是否用于搜索过滤
     * @return
     */
    @GetMapping("/params")
    public List<SpecParam> querySpecParam(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "generic", required = false) Boolean generic,
            @RequestParam(value = "searching", required = false) Boolean searching
    );

    /**
     * 根据cid查询参数组及其组下的参数
     * @param cid
     * @return
     */
    @GetMapping("/group/param/{cid}")
    public List<SpecGroup> queryGroupsWithParam(@PathVariable("cid")Long cid);
}
