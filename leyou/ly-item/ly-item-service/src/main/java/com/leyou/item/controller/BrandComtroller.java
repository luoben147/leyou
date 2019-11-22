package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.IBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/brand")
public class BrandComtroller {

    @Autowired
    private IBrandService brandService;

    /**
     * 根据查询条件分页并排序查询品牌信息
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    //http://api.leyou.com/api/item/brand/page?key=&page=1&rows=5&sortBy=id&desc=false
    @GetMapping("/page")
    public ResponseEntity<PageResult<Brand>> queryBrandsByPage(
            @RequestParam(value ="key",required = false)String key,
            @RequestParam(value ="page",defaultValue = "1")Integer page,
            @RequestParam(value ="rows",defaultValue = "5")Integer rows,
            @RequestParam(value ="sortBy",required = false)String sortBy,
            @RequestParam(value ="desc",required = false)Boolean desc
    ){

        PageResult<Brand> result=  brandService.queryBrandsByPage(key,page,rows,sortBy,desc);
        if(CollectionUtils.isEmpty(result.getItems())){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 新增品牌
     * @param brand
     * @param cids
     * @return
     */
    //http://api.leyou.com/api/item/brand
    //参数：{name: "嗷嗷", image: "", cids: "76,297", letter: "A"}
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("cids")List<Long> cids){
        brandService.saveBrand(brand,cids);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    /**
     * 对品牌进行修改
     * @param brand
     * @param cids
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        brandService.updateBrand(brand, cids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/bid/{bid}")
    public ResponseEntity<Void> deleteBrand(@PathVariable("bid")Long bid){
        brandService.deleteBrandByBid(bid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 根据分类查询品牌
     *  http://api.leyou.com/api/item/brand/cid/76
     */
    @GetMapping("/cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandsByCid(@PathVariable("cid")Long cid){
        List<Brand> brands= brandService.queryBrandsByCid(cid);
        if(CollectionUtils.isEmpty(brands)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brands);
    }

    /**
     * 根据id查询品牌信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id")Long id){
        Brand brand=  brandService.queryBrandById(id);
        if(brand==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brand);
    }

}
