package com.taoyoupin.sellergoods.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.taoyoupin.entity.PageResutl;
import com.taoyoupin.mapper.TbBrandMapper;
import com.taoyoupin.pojo.TbBrand;
import com.taoyoupin.pojo.TbBrandExample;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper brandMapper;
    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    @Override
    public PageResutl findPage(Integer curPage, Integer pageSize) {
        PageHelper.startPage(curPage,pageSize);

        Page<TbBrand> page = (Page<TbBrand>)brandMapper.selectByExample(null);

        return new PageResutl(page.getTotal(),page.getResult());
    }

    @Override
    public void add(TbBrand tbBrand) {
        brandMapper.insert(tbBrand);
    }

    @Override
    public TbBrand findOne(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(TbBrand tbBrand) {
        brandMapper.updateByPrimaryKey(tbBrand);
    }

    @Override
    public void delete(Long[] ids) {
       /* for (Long id : ids) {

            brandMapper.deleteByPrimaryKey(id);
        }*/

        TbBrandExample tbBrandExample = new TbBrandExample();
        TbBrandExample.Criteria criteria = tbBrandExample.createCriteria();
        criteria.andIdIn(Arrays.asList(ids));
        brandMapper.deleteByExample(tbBrandExample);


    }

    @Override
    public PageResutl search(TbBrand tbBrand,Integer curPage, Integer pageSize) {
        PageHelper.startPage(curPage,pageSize);

        TbBrandExample tbBrandExample = new TbBrandExample();

        TbBrandExample.Criteria criteria = tbBrandExample.createCriteria();

        if (tbBrand != null){
            if (StringUtils.isNotBlank(tbBrand.getName())){
                criteria.andNameLike("%"+tbBrand.getName()+"%");
            }
            if (StringUtils.isNotBlank(tbBrand.getFirstChar())){
                criteria.andFirstCharEqualTo(tbBrand.getFirstChar());
            }
        }

        Page<TbBrand> page = (Page<TbBrand>)brandMapper.selectByExample(tbBrandExample);

        return new PageResutl(page.getTotal(),page.getResult());
    }
}
