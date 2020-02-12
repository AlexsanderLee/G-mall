package com.neu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.neu.gmall.bean.PmsBaseSaleAttr;
import com.neu.gmall.bean.PmsProductInfo;
import com.neu.gmall.bean.PmsProductSaleAttr;
import com.neu.gmall.bean.PmsProductSaleAttrValue;
import com.neu.gmall.manage.mapper.PmsProductSaleAttrValueMapper;
import com.neu.gmall.service.SpuService;
import com.neu.gmall.manage.mapper.PmsBaseSaleAttrMapper;
import com.neu.gmall.manage.mapper.PmsProductInfoMapper;
import com.neu.gmall.manage.mapper.PmsProductSaleAttrMapper;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private PmsProductInfoMapper pmsProductInfoMapper;

    @Autowired
    private PmsProductSaleAttrMapper pmsProductSaleAttrMapper;

    @Autowired
    private PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;

    @Autowired
    private PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    @Override
    public List<PmsProductInfo> getSpuList(String catalog3Id){
        Long id = Long.parseLong(catalog3Id);
        Example e = new Example(PmsProductInfo.class);
        e.createCriteria().andEqualTo("catalog3Id",id);
        List<PmsProductInfo> pmsProductInfos = pmsProductInfoMapper.selectByExample(e);
        return pmsProductInfos;
    }

    @Override
    public List<PmsBaseSaleAttr> getSpuProductSaleAttr(){
        List<PmsBaseSaleAttr> pmsProductSaleAttrList = pmsBaseSaleAttrMapper.selectAll();
        return pmsProductSaleAttrList;
    }

    @Override
    public void saveSpuInfo(PmsProductInfo pmsProductInfo){
        pmsProductInfoMapper.insertSelective(pmsProductInfo);
        List<PmsProductSaleAttr> spuSaleAttrList = pmsProductInfo.getSpuSaleAttrList();
        //添加商品销售属性值:多个销售属性，每个属性有不同值
        for(PmsProductSaleAttr pmsProductSaleAttr:spuSaleAttrList){
            PmsProductSaleAttr temp = new PmsProductSaleAttr();
            temp.setProductId(pmsProductInfo.getId());
            temp.setSaleAttrId(pmsProductSaleAttr.getSaleAttrId());
            temp.setSaleAttrName(pmsProductSaleAttr.getSaleAttrName());
            pmsProductSaleAttrMapper.insertSelective(temp);
            List<PmsProductSaleAttrValue> pmsProductSaleAttrValueList = pmsProductSaleAttr.getSpuSaleAttrValueList();
            for(PmsProductSaleAttrValue pmsProductSaleAttrValue:pmsProductSaleAttrValueList){
                PmsProductSaleAttrValue p = new PmsProductSaleAttrValue();
                p.setProductId(pmsProductInfo.getId());
                p.setSaleAttrId(pmsProductSaleAttrValue.getSaleAttrId());
                p.setSaleAttrValueName(pmsProductSaleAttrValue.getSaleAttrValueName());
                pmsProductSaleAttrValueMapper.insertSelective(p);
            }

        }

    }
}
