package com.taoyoupin.sellergoods.service.impl;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.taoyoupin.mapper.*;
import com.taoyoupin.pojo.*;
import entity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.taoyoupin.pojo.TbGoodsExample.Criteria;
import com.taoyoupin.sellergoods.service.GoodsService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}


	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;


	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		goods.getGoods().setAuditStatus("0");//设置未申请状态
		goods.getGoods().setIsDelete("0");
		goodsMapper.insert(goods.getGoods());
		/*int i = 10/0;*/
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());//设置ID
		goodsDescMapper.insert(goods.getGoodsDesc());//插入商品扩展数据
		saveItemList(goods);
	}
	/**
	 * 插入SKU列表数据
	 * @param goods
	 */
	private void saveItemList(Goods goods) {
		if("1".equals(goods.getGoods().getIsEnableSpec())){

			for (TbItem tbItem : goods.getItemList()) {
				//标题
				String title= goods.getGoods().getGoodsName();

				Map<String,Object> specMap = JSON.parseObject(tbItem.getSpec());
				for(String key:specMap.keySet()){
					title+=" "+ specMap.get(key);
				}
				tbItem.setTitle(title);


				setItemValus(goods, tbItem);

				itemMapper.insert(tbItem);
			}
		}else {
			TbItem item=new TbItem();

			item.setTitle(goods.getGoods().getGoodsName());//商品KPU+规格描述串作为SKU名称
			item.setPrice( goods.getGoods().getPrice() );//价格
			item.setStatus("1");//状态
			item.setIsDefault("1");//是否默认
			item.setNum(99999);//库存数量
			item.setSpec("{}");

			setItemValus(goods,item);

			itemMapper.insert(item);

		}
	}

	private void setItemValus(Goods goods, TbItem tbItem) {
		tbItem.setGoodsId(goods.getGoods().getId());//商品SPU编号
		tbItem.setSellerId(goods.getGoods().getSellerId());//商家编号
		tbItem.setCategoryid(goods.getGoods().getCategory3Id());//商品分类编号（3级）
		tbItem.setCreateTime(new Date());//创建日期
		tbItem.setUpdateTime(new Date());//修改日期
		//品牌名称
		TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		tbItem.setBrand(brand.getName());
		//分类名称
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
		tbItem.setCategory(itemCat.getName());
		//商家名称
		TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		tbItem.setSeller(seller.getNickName());
		//图片地址（取spu的第一个图片）
		List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class) ;
		if(imageList.size()>0){
			tbItem.setImage ( (String)imageList.get(0).get("url"));
		}
	}


	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){

		goods.getGoods().setAuditStatus("0");//设置未申请状态:如果是经过修改的商品，需要重新设置状态,另外新启用方法来修改状态值。
		goodsMapper.updateByPrimaryKey(goods.getGoods());//保存商品表
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());//保存商品扩展表
		//删除原有的sku列表数据
		TbItemExample example=new TbItemExample();
		com.taoyoupin.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(example);
		//添加新的sku列表数据
		saveItemList(goods);//插入商品SKU列表数据
	}






	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods = new Goods();

		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);

		TbGoodsDescExample tbGoodsDescExample = new TbGoodsDescExample();
		TbGoodsDescExample.Criteria criteria = tbGoodsDescExample.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbGoodsDesc> tbGoodsDescs = goodsDescMapper.selectByExample(tbGoodsDescExample);

		TbItemExample itemExample = new TbItemExample();
		TbItemExample.Criteria criteria1 = itemExample.createCriteria();
		criteria1.andGoodsIdEqualTo(id);
		List<TbItem> tbItems = itemMapper.selectByExample(itemExample);

		goods.setGoods(tbGoods);
		if (tbGoodsDescs.size() > 0){
			goods.setGoodsDesc(tbGoodsDescs.get(0));
		}
		goods.setItemList(tbItems);

		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		/*for(Long id:ids){
			goodsMapper.deleteByPrimaryKey(id);
		}*/
        //最新的代码
        TbGoods goods = new TbGoods();
        goods.setIsDelete("1");

        TbGoodsExample example = new TbGoodsExample();//update tbgoods set is_delete=1 where id in (ids);
        example.createCriteria().andIdIn(Arrays.asList(ids));
        goodsMapper.updateByExampleSelective(goods,example);
        //关联SKU也要删除
        TbItemExample itemExample = new TbItemExample();
        TbItemExample.Criteria criteria = itemExample.createCriteria();
        criteria.andGoodsIdIn(Arrays.asList(ids));
        itemMapper.deleteByExample(itemExample);


    }
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();

		if (goods.getIsDelete()==null){
			//criteria.andIsDeleteNotIn(Arrays.asList("1"));
			criteria.andIsDeleteIsNull();//非删除状态
		}

		if(goods!=null){			
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteEqualTo(goods.getIsDelete());
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void updateStatus(Long[] ids, String status) {
		for(Long id:ids){
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(goods);
		}

	}

	@Override
	public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status) {
		TbItemExample example=new TbItemExample();
		com.taoyoupin.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdIn(Arrays.asList(goodsIds));
		criteria.andStatusEqualTo(status);
		return itemMapper.selectByExample(example);
	}


}
