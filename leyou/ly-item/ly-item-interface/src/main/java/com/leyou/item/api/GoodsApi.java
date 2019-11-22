package com.leyou.item.api;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 作为服务提供方提供调用接口
 */
public interface GoodsApi {
    /**
     * 商品列表的分页查询
     * 根据条件分页查询spu
     * http://api.leyou.com/api/item/spu/page?key=&saleable=true&page=1&rows=5
     */
    @GetMapping("/spu/page")
    public PageResult<SpuBo> querySpuByPage(
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "saleable",required = false)Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows
    );

    /**
     * 根据spuId查询spuDetail
     * http://api.leyou.com/api/item/spu/detail/2
     */
    @GetMapping("/spu/detail/{spuId}")
    public SpuDetail querySpuDetailBySpuId(@PathVariable("spuId")Long spuId);


    /**
     * 商品修改时的回显
     * 根据SpuId查询Sku作为回显数据
     * http://api.leyou.com/api/item/sku/list?id=2
     * @param spuId
     * @return
     */
    @GetMapping("/sku/list")
    public List<Sku> querySkusBySpuId(@RequestParam("id")Long spuId);

    /**
     * 根据spuId查询spu
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Spu querySpuById(@PathVariable("id")Long id);

    /**
     * 根据skuId查询sku
     * @param skuId
     * @return
     */
    @GetMapping("sku/{skuId}")
    public Sku querySkuBySkuId(@PathVariable("skuId")Long skuId);

}
