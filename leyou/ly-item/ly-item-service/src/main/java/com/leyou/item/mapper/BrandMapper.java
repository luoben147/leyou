package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface BrandMapper extends Mapper<Brand> {
    /**
     * 新增商品分类和品牌中间表数据
     * @param cid 商品分类id
     * @param bid 品牌id
     * @return
     */
    @Insert("insert into tb_category_brand(category_id,brand_id) values(#{cid},#{bid})")
    void insertCategoryBrand(@Param("cid") Long cid,@Param("bid") Long bid);
    /**
     * 根据brand id删除中间表相关数据
     * @param bid
     */
    @Delete("DELETE FROM tb_category_brand WHERE brand_id = #{bid}")
    void deleteCidByBid(Long bid);

    @Select("select * from tb_brand as a inner join tb_category_brand as b on b.brand_id=a.id where b.category_id=#{id}")
    List<Brand> selectBrandsByCid(Long cid);
}
