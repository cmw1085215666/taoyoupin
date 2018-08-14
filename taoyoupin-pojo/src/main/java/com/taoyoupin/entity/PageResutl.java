package com.taoyoupin.entity;

import com.taoyoupin.pojo.TbBrand;

import java.io.Serializable;
import java.util.List;

public class PageResutl implements Serializable {

    private Long total;

    private List rows;

    public PageResutl(Long total, List rows) {
        this.total = total;
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }
}
