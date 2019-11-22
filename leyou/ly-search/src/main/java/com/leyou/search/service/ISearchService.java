package com.leyou.search.service;

import com.leyou.item.pojo.Spu;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;


public interface ISearchService {

    public Goods buildGoods(Spu spu) throws Exception;

    SearchResult search(SearchRequest request);

    void save(Long id)throws Exception;

    void delete(Long id);
}
