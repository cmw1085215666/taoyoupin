package com.taoyoupin.search.service;

import java.util.List;
import java.util.Map;

/**
 * @author yin
 * @Date 2018/8/21 15:46
 * @Method
 */
public interface ItemSearchService {

    public Map<String, Object> search(Map searchMap);

    void importList(List list);

    void deleteByGoodsIds(List<Long> longs);
}
