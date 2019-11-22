package com.leyou.search.pojo;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;

import java.util.List;
import java.util.Map;

public class SearchResult extends PageResult<Goods> {

    //分类集合
    private List<Map<String,Object>> categorys;

    //品牌集合
    private List<Brand> brands;

    //规格参数
    private List<Map<String,Object>> specs;

    public SearchResult() {
    }


    public SearchResult(List<Map<String, Object>> categorys, List<Brand> brands, List<Map<String, Object>> specs) {
        this.categorys = categorys;
        this.brands = brands;
        this.specs = specs;
    }

    public SearchResult(Long total, List<Goods> items, List<Map<String, Object>> categorys, List<Brand> brands, List<Map<String, Object>> specs) {
        super(total, items);
        this.categorys = categorys;
        this.brands = brands;
        this.specs = specs;
    }

    public SearchResult(Long total, Integer totalPage, List<Goods> items, List<Map<String, Object>> categorys, List<Brand> brands, List<Map<String, Object>> specs) {
        super(total, totalPage, items);
        this.categorys = categorys;
        this.brands = brands;
        this.specs = specs;
    }

    public List<Map<String, Object>> getCategorys() {
        return categorys;
    }

    public void setCategorys(List<Map<String, Object>> categorys) {
        this.categorys = categorys;
    }

    public List<Brand> getBrands() {
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }

    public List<Map<String, Object>> getSpecs() {
        return specs;
    }

    public void setSpecs(List<Map<String, Object>> specs) {
        this.specs = specs;
    }
}
