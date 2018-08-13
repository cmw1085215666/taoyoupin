package com.taoyoupin.entity;

import com.taoyoupin.pojo.TbBrand;

import java.io.Serializable;
import java.util.List;

public class PageResutl implements Serializable {

    private Long total;

    private List<TbBrand> rows;

    public PageResutl(Long total, List<TbBrand> rows) {
        this.total = total;
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<TbBrand> getRows() {
        return rows;
    }

    public void setRows(List<TbBrand> rows) {
        this.rows = rows;
    }
}
