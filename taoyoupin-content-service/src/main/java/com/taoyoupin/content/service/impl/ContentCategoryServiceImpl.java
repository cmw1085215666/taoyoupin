package com.taoyoupin.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.taoyoupin.mapper.TbContentCategoryMapper;
import com.taoyoupin.pojo.TbContentCategory;
import com.taoyoupin.pojo.TbContentCategoryExample;
import com.taoyoupin.pojo.TbContentCategoryExample.Criteria;
import com.taoyoupin.content.service.ContentCategoryService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContentCategory> findAll() {
		return contentCategoryMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContentCategory> page=   (Page<TbContentCategory>) contentCategoryMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContentCategory contentCategory) {
		contentCategoryMapper.insert(contentCategory);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContentCategory contentCategory){
		contentCategoryMapper.updateByPrimaryKey(contentCategory);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContentCategory findOne(Long id){
		return contentCategoryMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			contentCategoryMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContentCategory contentCategory, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentCategoryExample example=new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		
		if(contentCategory!=null){			
						if(contentCategory.getName()!=null && contentCategory.getName().length()>0){
				criteria.andNameLike("%"+contentCategory.getName()+"%");
			}
			if(contentCategory.getContentGroup()!=null && contentCategory.getContentGroup().length()>0){
				criteria.andContentGroupLike("%"+contentCategory.getContentGroup()+"%");
			}
			if(contentCategory.getContentKey()!=null && contentCategory.getContentKey().length()>0){
				criteria.andContentKeyLike("%"+contentCategory.getContentKey()+"%");
			}
			if(contentCategory.getStatus()!=null && contentCategory.getStatus().length()>0){
				criteria.andStatusLike("%"+contentCategory.getStatus()+"%");
			}
	
		}
		
		Page<TbContentCategory> page= (Page<TbContentCategory>)contentCategoryMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
