package com.leyou.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import com.leyou.item.service.ICategoryService;
import com.leyou.item.service.IGoodsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsServiceImpl implements IGoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 商品列表的分页查询
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @Override
    public PageResult<SpuBo> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows) {

        Example example=new Example(Spu.class);
        Example.Criteria criteria=example.createCriteria();
        //添加查询条件
        if(StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        //添加上下架的过滤条件
        if(saleable!=null){
            criteria.andEqualTo("saleable",saleable);
        }
        //添加分页
        PageHelper.startPage(page,rows);

        //执行查询，获取spu集合
        List<Spu> spus = spuMapper.selectByExample(example);
        PageInfo<Spu> pageInfo=new PageInfo<>(spus);

        //转化成spubo集合
        List<SpuBo> spuBos = spus.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            //属性拷贝，将spu中封装的属性拷贝到spuBo中
            BeanUtils.copyProperties(spu, spuBo);
            //查询品牌名称
            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());
            //查询分类名称
            List<String> names = categoryService.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBo.setCname(StringUtils.join(names, "-"));
            return spuBo;
        }).collect(Collectors.toList());

        //返回PageResult<SpuBo>
        return new PageResult<>(pageInfo.getTotal(),spuBos);
    }

    /**
     * 新增商品
     * @param spuBo
     */
    @Transactional
    @Override
    public void saveGoods(SpuBo spuBo) {

        //先新增spu    数据中缺少saleable，valid,createtime,lastupdatetime
        spuBo.setId(null);
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        spuMapper.insertSelective(spuBo);
        //新增spu_detail
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        spuDetailMapper.insertSelective(spuDetail);

        saveSkuAndStock(spuBo);

        sendMsg("insert",spuBo.getId());
    }

    /**
     * 新增sku和stock
     * @param spuBo
     */
    private void saveSkuAndStock(SpuBo spuBo) {
        spuBo.getSkus().forEach(sku -> {
            //新增sku
            sku.setId(null);
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            skuMapper.insertSelective(sku);
            //新增stock
            Stock stock=new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockMapper.insertSelective(stock);
        });
    }

    /**
     * 根据spuId查询spuDetail
     */
    @Override
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        return spuDetailMapper.selectByPrimaryKey(spuId);
    }

    /**
     * 商品修改时的回显
     * 根据SpuId查询Sku作为回显数据
     * @param spuId
     * @return
     */
    @Override
    public List<Sku> querySkusBySpuId(Long spuId) {
        Sku record=new Sku();
        record.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(record);
        skus.forEach(sku -> {
            Stock stock = stockMapper.selectByPrimaryKey(sku.getId());
            sku.setStock(stock.getStock());
        });
        return skus;
    }

    /**
     * 修改商品
     * @param spuBo
     */
    @Transactional
    @Override
    public void updateGoods(SpuBo spuBo) {
        //根据spuId查询要删除的sku
        Sku record=new Sku();
        record.setSpuId(spuBo.getId());
        List<Sku> skus = skuMapper.select(record);
        skus.forEach(sku -> {
            //删除库存stock
            stockMapper.deleteByPrimaryKey(sku.getId());
        });

        //删除原有的Sku
        Sku sku = new Sku();
        sku.setSpuId(spuBo.getId());
        skuMapper.delete(sku);

        //新增sku和stock
        saveSkuAndStock(spuBo);

        //更新spu和spuDetail
        spuBo.setCreateTime(null);//防止sql注入
        spuBo.setLastUpdateTime(new Date());
        spuBo.setSaleable(null);
        spuBo.setValid(null);
        spuMapper.updateByPrimaryKeySelective(spuBo);
        spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());

        sendMsg("update",spuBo.getId());
    }

    /**
     * 删除商品
     * 逻辑删除 valid状态改为0
     * @param spuId
     */
    @Transactional
    @Override
    public void deleteGoods(Long spuId) {
        Spu spu=new Spu();
        spu.setId(spuId);
        spu.setValid(false);
        spu.setLastUpdateTime(new Date());
        spuMapper.updateByPrimaryKeySelective(spu);

        //sendMsg("delete",spuId);

        //物理删除
        //先删除sku和库存信息
       /* deleteStockAndSku(spuId);
        //再删除spu和spu_detail信息
        spuMapper.deleteByPrimaryKey(spuId);
        spuDetailMapper.deleteByPrimaryKey(spuId);
        sendMsg( "delete",spuId);*/
    }


    /**
     * 根据spuId删除sku和其库存信息
     *
     * @param spuId
     */
    private void deleteStockAndSku(Long spuId) {
        //查询sku
        Sku querySku = new Sku();
        querySku.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(querySku);
        //如果查询的skus不为空，则拿到其id进行库存删除
        //JDK1.8新特性写法
        if (!CollectionUtils.isEmpty(skus)) {
            List<Long> ids = skus.stream().map(sku -> sku.getId()).collect(Collectors.toList());
            Example example = new Example(Stock.class);
            example.createCriteria().andIn("skuId", ids);
            stockMapper.deleteByExample(example);

        }
        skuMapper.delete(querySku);
    }

    /**
     * 商品上下架
     * @param spuId
     */
    @Override
    public void changeSaleable(Long spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        spu.setSaleable(!spu.getSaleable());
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    @Override
    public Spu querySpuById(Long id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    @Override
    public Sku querySkuBySkuId(Long skuId) {
        return skuMapper.selectByPrimaryKey(skuId);
    }


    /**
     * 发送消息,发到配置文件中的默认交换机
     * @param id   变更的商品id
     * @param type 变更类型：CRUD
     */
    private void sendMsg(String type,Long id) {
        try {
            amqpTemplate.convertAndSend("item."+type,id);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }


}
