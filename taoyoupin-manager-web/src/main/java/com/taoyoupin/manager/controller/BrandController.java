package com.taoyoupin.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.taoyoupin.entity.PageResutl;
import com.taoyoupin.entity.Resutl;
import com.taoyoupin.pojo.TbBrand;
import com.taoyoupin.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference
    private BrandService brandService;
    /**
     * 返回全部列表
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    @RequestMapping("/findPage")
    public PageResutl findPage(Integer page, Integer rows){
        return brandService.findPage(page,rows);
    }

    @RequestMapping("/add")
    public Resutl add(@RequestBody TbBrand tbBrand){

        try {
            brandService.add(tbBrand);
            return new Resutl(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Resutl(false,"添加失败");
    }

    @RequestMapping("/update")
    public Resutl update(@RequestBody TbBrand tbBrand){

        try {
            brandService.update(tbBrand);
            return new Resutl(true,"更新成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Resutl(false,"更新失败");
    }

    @RequestMapping("/findOne")
    public TbBrand findOne(Long id){
        return brandService.findOne(id);
    }

    @RequestMapping("/delete")
    public Resutl delete(Long[] ids){

        try {
            brandService.delete(ids);
            return new Resutl(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Resutl(false,"删除失败");
    }
}