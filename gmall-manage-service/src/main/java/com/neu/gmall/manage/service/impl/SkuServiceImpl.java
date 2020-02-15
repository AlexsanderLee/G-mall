package com.neu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.neu.gmall.bean.*;
import com.neu.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.neu.gmall.manage.mapper.PmsSkuImageMapper;
import com.neu.gmall.manage.mapper.PmsSkuInfoMapper;
import com.neu.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.neu.gmall.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    private PmsSkuImageMapper pmsSkuImageMapper;

    @Autowired
    private PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    private PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo){
        //存储sku_info
        pmsSkuInfo.setProductId(Long.parseLong(pmsSkuInfo.getSpuId()));
        pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
        //存储图片信息
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for(PmsSkuImage pmsSkuImage:skuImageList){
            pmsSkuImage.setSkuId(pmsSkuInfo.getId());
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }

        for(PmsSkuAttrValue pmsSkuAttrValue:skuAttrValueList){
            pmsSkuAttrValue.setSkuId(pmsSkuInfo.getId());
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }

        for(PmsSkuSaleAttrValue pmsSkuSaleAttrValue:skuSaleAttrValueList){
            pmsSkuSaleAttrValue.setSkuId(pmsSkuInfo.getId());
            pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }

    }

    //不采用缓存
    public PmsSkuInfo getSkuByIdFromDB(String skuId){
        PmsSkuInfo skuInfoList=null;
        Long id =Long.parseLong(skuId);
        try {
            skuInfoList = pmsSkuInfoMapper.selectByPrimaryKey(id);
            PmsSkuImage pmsSkuImage = new PmsSkuImage();
            pmsSkuImage.setSkuId(id);
            List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.select(pmsSkuImage);
            skuInfoList.setSkuImageList(pmsSkuImages);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return skuInfoList;
    }

    @Override
    public PmsSkuInfo getSkuById(String skuId){
        PmsSkuInfo skuInfoList=null;
        Long id =Long.parseLong(skuId);

        //连接缓存

        //查询缓存

        //缓存没有则查询DB


        return skuInfoList;
    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(Long productId){
        List<PmsSkuInfo> pmsSkuInfoList=pmsSkuInfoMapper.selectSkuSaleAttrValueListBySpu(productId);
        return pmsSkuInfoList;
    }


}
