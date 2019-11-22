package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.IGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class GoodController {

    @Autowired
    private IGoodsService goodsService;

    /**
     * 商品列表的分页查询
     * 根据条件分页查询spu
     * http://api.leyou.com/api/item/spu/page?key=&saleable=true&page=1&rows=5
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuBo>> querySpuByPage(
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "saleable",required = false)Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows
    ){
        PageResult<SpuBo> result=  goodsService.querySpuByPage(key,saleable,page,rows);

        if(result==null||CollectionUtils.isEmpty(result.getItems())){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 新增商品
     * http://api.leyou.com/api/item/goods
     */
    @Transactional
    @PostMapping("/goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spuBo){
        goodsService.saveGoods(spuBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改商品 -》先删除原有的商品sku和stock，再新增，最后修改spu
     * @param spuBo
     * @return
     */
    @Transactional
    @PutMapping("/goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuBo spuBo){
        goodsService.updateGoods(spuBo);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);//更新、删除成功响应204
    }


    /**
     * 根据spuId查询spuDetail
     * http://api.leyou.com/api/item/spu/detail/2
     */
    @GetMapping("/spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable("spuId")Long spuId){
        SpuDetail spuDetail=  goodsService.querySpuDetailBySpuId(spuId);
        if(spuDetail==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spuDetail);
    }

    /**
     * 商品修改时的回显
     * 根据SpuId查询Sku作为回显数据
     * http://api.leyou.com/api/item/sku/list?id=2
     * @param spuId
     * @return
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> querySkusBySpuId(@RequestParam("id")Long spuId){
        List<Sku> skus=  goodsService.querySkusBySpuId(spuId);
        if(CollectionUtils.isEmpty(skus)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(skus);
    }

    /**
     * 删除商品
     * @param spuId
     * @return
     */
    //http://api.leyou.com/api/item/goods/spu/195
    @Transactional
    @DeleteMapping("/goods/spu/{spuId}")
    public ResponseEntity<Void> deleteGoods(@PathVariable("spuId") Long spuId) {
        goodsService.deleteGoods(spuId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 商品上下架
     */
    //http://api.leyou.com/api/item/goods/spu/out/195
    @PutMapping("/goods/spu/out/{spuId}")
    public ResponseEntity<Void> changeSaleable(@PathVariable("spuId")Long spuId){
        goodsService.changeSaleable(spuId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id")Long id){
        Spu spu= goodsService.querySpuById(id);
        if(spu==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spu);
    }

    @GetMapping("sku/{skuId}")
    public ResponseEntity<Sku> querySkuBySkuId(@PathVariable("skuId")Long skuId){
        Sku sku= goodsService.querySkuBySkuId(skuId);
        if(sku==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(sku);
    }


}
