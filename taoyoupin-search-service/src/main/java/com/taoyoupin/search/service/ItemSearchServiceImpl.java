package com.taoyoupin.search.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.taoyoupin.mapper.TbItemCatMapper;
import com.taoyoupin.mapper.TbSpecificationOptionMapper;
import com.taoyoupin.mapper.TbTypeTemplateMapper;
import com.taoyoupin.pojo.*;
import com.taoyoupin.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yin
 * @Date 2018/8/21 15:58
 * @Method
 */
@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    @Override
    public void deleteByGoodsIds(List<Long> goodsIdList) {
        System.out.println("删除商品Id"+goodsIdList);
        SimpleQuery query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    @Override
    public void importList(List list) {
        solrTemplate.saveBean(list);
        solrTemplate.commit();
    }

    @Override
    public Map<String, Object> search(Map searchMap) {
        String keywords = (String) searchMap.get("keywords");
        if (keywords!=null){

        searchMap.put("keywords", keywords.replace(" ", ""));
        }
        HashMap<String, Object> map = new HashMap<>();
        map.putAll(searchList(searchMap));
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList", categoryList);
       /* if (categoryList.size() > 0) {
            map.putAll(searchBrandAndSpecList(categoryList.get(0)));
        }*/
        String categoryName = (String) searchMap.get("category");
        if (!"".equals(categoryName)) {
            map.putAll(searchBrandAndSpecList(categoryName));
        }else{
            if (null != categoryList && categoryList.size() > 0) {
                map.putAll(searchBrandAndSpecList(categoryList.get(0)));
            }


        }

        return map;
    }

    private Map searchList(Map searchMap) {
        HashMap map = new HashMap();
        SimpleHighlightQuery query = new SimpleHighlightQuery();
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        query.setHighlightOptions(highlightOptions);
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        if(!"".equals(searchMap.get("category"))){
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        if (!"".equals(searchMap.get("brand"))) {
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        if (null != searchMap.get("spec")) {
            Map<String,String> specMap= (Map<String, String>) searchMap.get("spec");
            for (String s : specMap.keySet()) {
                Criteria filterCriteria = new Criteria("item_spec_" + s).is(specMap.get(s));
                SimpleFilterQuery simpleQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(simpleQuery);

            }
        }

        String price = (String) searchMap.get("price");
        if (null != price && !"".equals(price)) {

            String[] prices = price.split("-");
            Criteria filterCriteria=null;
            if ("*".equals(prices[1])) {
                filterCriteria = new Criteria("item_price").greaterThanEqual(prices[0]);
            }else{
                filterCriteria =new Criteria("item_price").between(prices[0], prices[1],true,true);
            }
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);

        }
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if (pageNo == null) {
            pageNo=1;
        }

        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageSize == null) {
            pageSize=20;
        }

        query.setOffset((pageNo - 1) * pageSize);
        query.setRows(pageSize);

        String sortValue = (String) searchMap.get("sort");
        String sortField = (String) searchMap.get("sortField");
        if (sortValue.equals("ASC")) {
            Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
            query.addSort(sort);
        }

        if (sortValue.equals("DESC")) {
            Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
            query.addSort(sort);
        }


        HighlightPage<TbItem> tbItems = solrTemplate.queryForHighlightPage(query, TbItem.class);
        for (HighlightEntry<TbItem> h : tbItems.getHighlighted()) {
            TbItem item = h.getEntity();
            if (h.getHighlights().size() > 0 && h.getHighlights().get(0).getSnipplets().size() > 0) {
                item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));
            }
        }


        map.put("pageNo", pageNo);
        map.put("rows", tbItems.getContent());
        map.put("totalPages", tbItems.getTotalPages());
        map.put("total", tbItems.getTotalElements());

        return map;
    }

    private List searchCategoryList(Map searchMap) {
        List<String> list = new ArrayList<>();
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        GroupOptions options = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(options);
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : content) {
            list.add(entry.getGroupValue());
        }
        return list;

    }

    private Map searchBrandAndSpecList(String category) {
        HashMap map = new HashMap<>();
        Long typeId = null;
        try {
            typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (typeId == null) {
            List<TbItemCat> list = itemCatMapper.selectByExample(null);
            for (TbItemCat tbItemCat : list) {
                redisTemplate.boundHashOps("itemCat").put(tbItemCat.getName(), tbItemCat.getTypeId());
            }
            typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        }

        if (typeId != null) {
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            List<Map> specList= (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);

            if (brandList == null || brandList.size() <=0 ||specList==null ||specList.size()<=0) {
                List<TbTypeTemplate> typeTemplateList = typeTemplateMapper.selectByExample(null);
                for (TbTypeTemplate tbTypeTemplate : typeTemplateList) {
                    List<Map> brandListdb = JSON.parseArray(tbTypeTemplate.getBrandIds(), Map.class);
                    redisTemplate.boundHashOps("brandList").put(tbTypeTemplate.getId(), brandListdb);
                      specList = findSpecList(tbTypeTemplate.getId());
                    redisTemplate.boundHashOps("specList").put(tbTypeTemplate.getId(), specList);
                }
                brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            }

            map.put("brandList", brandList);
            map.put("specList",specList);
        }
        return map;
    }


    private List<Map> findSpecList(Long id) {
        TbTypeTemplate tbTypeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
        List<Map> list = JSON.parseArray(tbTypeTemplate.getSpecIds(), Map.class);
        for (Map map : list) {
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(new Long((Integer) map.get("id")));
            List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(example);
            map.put("option", options);
        }
        return list;
    }


}
