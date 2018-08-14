package com.taoyoupin.sellergoods.service;



import com.taoyoupin.entity.PageResutl;
import com.taoyoupin.pojo.TbBrand;

import java.util.List;

public interface BrandService {

    public List<TbBrand> findAll();

    PageResutl findPage(Integer curPage,Integer pageSize);

    void add(TbBrand tbBrand);

    TbBrand findOne(Long id);

    void update(TbBrand tbBrand);

    void delete(Long[] ids);

    PageResutl search(TbBrand tbBrand,Integer curPage,Integer pageSize);

}
