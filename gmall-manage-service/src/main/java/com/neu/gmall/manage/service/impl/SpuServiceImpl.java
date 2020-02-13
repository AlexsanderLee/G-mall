package com.neu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.neu.gmall.bean.*;
import com.neu.gmall.manage.mapper.*;
import com.neu.gmall.service.SpuService;
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

    @Autowired
    private PmsProductImageMapper pmsProductImageMapper;

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
        //保存上传图片的信息：spuImageList
        for(PmsProductImage pmsProductImage:pmsProductInfo.getSpuImageList()){
            PmsProductImage p =new PmsProductImage();
            p.setImgName(pmsProductImage.getImgName());
            p.setImgUrl(pmsProductImage.getImgUrl());
            p.setProductId(pmsProductInfo.getId());
            pmsProductImageMapper.insertSelective(p);
        }

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
    @Override
    public List<PmsProductSaleAttr> getSpuSaleAttrList(String spuId){
        PmsProductSaleAttr pmsProductSaleAttr =new PmsProductSaleAttr();
        pmsProductSaleAttr.setProductId(Long.parseLong(spuId));
        List<PmsProductSaleAttr> select = pmsProductSaleAttrMapper.select(pmsProductSaleAttr);
        for(PmsProductSaleAttr p:select){
            PmsProductSaleAttrValue v = new PmsProductSaleAttrValue();
            v.setProductId(p.getProductId());
            //此处为销售属性的id，不为主键，理清表的关系
            v.setSaleAttrId(p.getSaleAttrId());
            p.setSpuSaleAttrValueList(pmsProductSaleAttrValueMapper.select(v));
        }
        return select;
    }

    @Override
    public List<PmsProductImage> getSpuImageList(String spuId){
        PmsProductImage pmsProductImage = new PmsProductImage();
        pmsProductImage.setProductId(Long.parseLong(spuId));
        List<PmsProductImage> pmsProductImages = pmsProductImageMapper.select(pmsProductImage);
        return pmsProductImages;
    }
}
