package com.taoyoupin.sellergoods.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.taoyoupin.mapper.TbSpecificationOptionMapper;
import com.taoyoupin.pojo.TbSpecificationOption;
import com.taoyoupin.pojo.TbSpecificationOptionExample;
import com.taoyoupin.pojogroup.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.taoyoupin.mapper.TbSpecificationMapper;
import com.taoyoupin.pojo.TbSpecification;
import com.taoyoupin.pojo.TbSpecificationExample;
import com.taoyoupin.pojo.TbSpecificationExample.Criteria;
import com.taoyoupin.sellergoods.service.SpecificationService;

import entity.PageResult;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper specificationMapper;

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbSpecification> findAll() {
        return specificationMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(Specification specification) {
        //1.获取规格的数据
        TbSpecification specificationData = specification.getSpecification();
        //2.获取规格选项的数据
        List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();

        //3.插入到规格表
        specificationMapper.insert(specificationData);
        //4.插入到规格选项表
        for (TbSpecificationOption option : specificationOptionList) {
            option.setSpecId(specificationData.getId());
            specificationOptionMapper.insert(option);
        }
    }


    /**
     * 修改
     */
    @Override
    public void update(Specification specification) {
        //1.获取规格数据 更新
        TbSpecification tbSpecification = specification.getSpecification();
        specificationMapper.updateByPrimaryKey(tbSpecification);

        //2.获取规格对应的规格选项数据  更新

        //先删除原来的规格下的所有的选项
        TbSpecificationOptionExample exmaple = new TbSpecificationOptionExample();
        exmaple.createCriteria().andSpecIdEqualTo(tbSpecification.getId());
        specificationOptionMapper.deleteByExample(exmaple);//delete from option where specId =1
        //再新增新的规格选项
        for (TbSpecificationOption option : specification.getSpecificationOptionList()) {
            option.setSpecId(tbSpecification.getId());
            specificationOptionMapper.insert(option);
        }
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Specification findOne(Long id) {
        //查询规格的数据
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        //再根据规格的id查询规格选项列表
        TbSpecificationOptionExample exmaple = new TbSpecificationOptionExample();
        exmaple.createCriteria().andSpecIdEqualTo(id);
        List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(exmaple);//select *from option where sepcId=1
        //返回组合对象
        Specification specification = new Specification();
        specification.setSpecification(tbSpecification);
        specification.setSpecificationOptionList(options);
        return specification;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            specificationMapper.deleteByPrimaryKey(id);
            TbSpecificationOptionExample exmaple = new TbSpecificationOptionExample();
            exmaple.createCriteria().andSpecIdEqualTo(id);
            specificationOptionMapper.deleteByExample(exmaple);
        }
    }


    @Override
    public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSpecificationExample example = new TbSpecificationExample();
        Criteria criteria = example.createCriteria();

        if (specification != null) {
            if (specification.getSpecName() != null && specification.getSpecName().length() > 0) {
                criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
            }

        }

        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<Map> findSpecList() {
        List<Map> mapList = new ArrayList<>();
        List<TbSpecification> specifications = specificationMapper.selectByExample(null);
        for (TbSpecification specification : specifications) {
            Map map =  new HashMap();
            map.put("id",specification.getId());
            map.put("text",specification.getSpecName());
            mapList.add(map);
        }
        return mapList;
    }

}
