package com.neu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.neu.gmall.bean.PmsBaseCatalog1;
import com.neu.gmall.bean.PmsBaseCatalog2;
import com.neu.gmall.bean.PmsBaseCatalog3;
import com.neu.gmall.manage.mapper.PmsBaseCatalog1Mapper;
import com.neu.gmall.manage.mapper.PmsBaseCatalog2Mapper;
import com.neu.gmall.manage.mapper.PmsBaseCatalog3Mapper;
import com.neu.gmall.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
@Service
public class CatalogServiceImpl implements CatalogService {

    @Autowired
    private  PmsBaseCatalog1Mapper pmsBaseCatalog1Mapper;

    @Autowired
    private PmsBaseCatalog2Mapper pmsBaseCatalog2Mapper;

    @Autowired
    private PmsBaseCatalog3Mapper pmsBaseCatalog3Mapper;

    public List<PmsBaseCatalog1> getCatalog1(){
        return pmsBaseCatalog1Mapper.selectAll();
    }

    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id){
//        Example e = new Example(PmsBaseCatalog2.class);
//        e.createCriteria().andEqualTo("catalog1Id",);
//        List<PmsBaseCatalog2> pmsBaseCatalog2s = pmsBaseCatalog2Mapper.selectByExample(e);
        PmsBaseCatalog2 pmsBaseCatalog2 = new PmsBaseCatalog2();
        pmsBaseCatalog2.setCatalog1Id(Integer.parseInt(catalog1Id));
        List<PmsBaseCatalog2> select = pmsBaseCatalog2Mapper.select(pmsBaseCatalog2);
        return select;
    }

    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id){
//        Example e =new Example(PmsBaseCatalog3.class);
//        e.createCriteria().andEqualTo("catalog2Id",Long.parseLong(catalog2Id));
//        List<PmsBaseCatalog3> pmsBaseCatalog3s = pmsBaseCatalog3Mapper.selectByExample(e);
        PmsBaseCatalog3 pmsBaseCatalog2 = new PmsBaseCatalog3();
        pmsBaseCatalog2.setCatalog2Id(Long.parseLong(catalog2Id));
        List<PmsBaseCatalog3> select = pmsBaseCatalog3Mapper.select(pmsBaseCatalog2);
        return  select;
    }
}
