package com.taoyoupin.solr;

import com.alibaba.fastjson.JSON;
import com.taoyoupin.mapper.TbItemMapper;
import com.taoyoupin.pojo.TbItem;
import com.taoyoupin.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;

import java.util.List;
import java.util.Map;

public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 导入商品数据到索引库中
     */
   /* public void importItemData() {
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");//已审核
        List<TbItem> itemList = itemMapper.selectByExample(example);
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }*/
    /**
     * 导入商品数据
     */
    public void importItemData(){
        TbItemExample example=new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");//已审核
        List<TbItem> itemList = itemMapper.selectByExample(example);
        System.out.println("===商品列表===");
        for(TbItem item:itemList){

            Map specMap= JSON.parseObject(item.getSpec());//将spec字段中的json字符串转换为map
            item.setSpecMap(specMap);//给带注解的字段赋值
            System.out.println(item.getTitle());
        }
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
        System.out.println("===结束===");
    }
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
        context.getBean(SolrUtil.class).importItemData();
    }



}
