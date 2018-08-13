package com.taoyoupin.sellergoods.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.taoyoupin.entity.PageResutl;
import com.taoyoupin.mapper.TbBrandMapper;
import com.taoyoupin.pojo.TbBrand;
import org.springframework.beans.factory.annotation.Autowired;

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
        for (Long id : ids) {

            brandMapper.deleteByPrimaryKey(id);
        }
    }


}
