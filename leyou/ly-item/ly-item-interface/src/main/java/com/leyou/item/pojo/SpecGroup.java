package com.leyou.item.pojo;

import javax.persistence.*;
import java.util.List;

@Table(name = "tb_spec_group")
public class SpecGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long cid;

    private String name;

    //表中没有该字段 忽略该字段
    @Transient
    private List<SpecParam> specParams;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SpecParam> getSpecParams() {
        return specParams;
    }

    public void setSpecParams(List<SpecParam> specParams) {
        this.specParams = specParams;
    }
}
