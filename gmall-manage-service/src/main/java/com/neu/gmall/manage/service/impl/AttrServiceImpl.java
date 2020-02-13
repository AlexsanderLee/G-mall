package com.neu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.neu.gmall.bean.PmsBaseAttrInfo;
import com.neu.gmall.bean.PmsBaseAttrValue;
import com.neu.gmall.bean.PmsBaseSaleAttr;
import com.neu.gmall.bean.PmsSkuInfo;
import com.neu.gmall.manage.mapper.PmsBaseAttrInfoMapper;
import com.neu.gmall.manage.mapper.PmsBaseAttrValueMapper;
import com.neu.gmall.service.AttrService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Service
public class AttrServiceImpl implements AttrService {

    @Autowired
    private PmsBaseAttrInfoMapper attrInfoMapper;

    @Autowired
    private PmsBaseAttrValueMapper attrValueMapper;

    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id){
        PmsBaseAttrInfo pmsBaseAttrInfo = new PmsBaseAttrInfo();
        pmsBaseAttrInfo.setCatalog3Id(Long.parseLong(catalog3Id));
        List<PmsBaseAttrInfo> select = attrInfoMapper.select(pmsBaseAttrInfo);
        for(PmsBaseAttrInfo p : select){
            PmsBaseAttrValue v = new PmsBaseAttrValue();
            v.setAttrId(p.getId());
            p.setAttrValueList(attrValueMapper.select(v));
        }
        return  select;
    }

    //insertSelective将不为空的部分插入数据库，insert全插入
    public void saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {

        Long id = pmsBaseAttrInfo.getId();
        //通过前端返回的json格式分析不同状态
        if (id == null) {
            // id为空，保存
            // 保存属性
            attrInfoMapper.insertSelective(pmsBaseAttrInfo);//insert insertSelective 是否将null插入数据库

            // 保存属性值
            List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
            for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo.getId());

                attrValueMapper.insertSelective(pmsBaseAttrValue);
            }
        } else {
            // id不空，修改

            // 属性修改
            Example example = new Example(PmsBaseAttrInfo.class);
            example.createCriteria().andEqualTo("id", pmsBaseAttrInfo.getId());
            attrInfoMapper.updateByExampleSelective(pmsBaseAttrInfo, example);


            // 属性值修改
            // 按照属性id删除所有属性值
            PmsBaseAttrValue pmsBaseAttrValueDel = new PmsBaseAttrValue();
            pmsBaseAttrValueDel.setAttrId(pmsBaseAttrInfo.getId());
            attrValueMapper.delete(pmsBaseAttrValueDel);

            // 删除后，将新的属性值插入
            List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
            for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo.getId());
                attrValueMapper.insertSelective(pmsBaseAttrValue);
            }
        }
    }

//改进：修改属性时，相同的保留，不同的修改：




    public List<PmsBaseAttrValue> getAttrValueList(String attrId){
        PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
        pmsBaseAttrValue.setAttrId(Long.parseLong(attrId));
        List<PmsBaseAttrValue> select = attrValueMapper.select(pmsBaseAttrValue);
        return  select;
    }
}
