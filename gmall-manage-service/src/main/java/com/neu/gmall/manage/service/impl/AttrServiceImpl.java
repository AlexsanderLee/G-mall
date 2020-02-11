package com.neu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.neu.gmall.bean.PmsBaseAttrInfo;
import com.neu.gmall.manage.mapper.PmsBaseAttrInfoMapper;
import com.neu.gmall.service.AttrService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class AttrServiceImpl implements AttrService {

    @Autowired
    private PmsBaseAttrInfoMapper attrInfoMapper;

    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id){
        PmsBaseAttrInfo pmsBaseAttrInfo = new PmsBaseAttrInfo();
        pmsBaseAttrInfo.setCatalog3Id(Long.parseLong(catalog3Id));
        List<PmsBaseAttrInfo> select = attrInfoMapper.select(pmsBaseAttrInfo);
        return  select;
    }
}
