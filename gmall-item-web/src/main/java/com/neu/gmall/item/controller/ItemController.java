package com.neu.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.neu.gmall.bean.PmsProductSaleAttr;
import com.neu.gmall.bean.PmsSkuAttrValue;
import com.neu.gmall.bean.PmsSkuInfo;
import com.neu.gmall.bean.PmsSkuSaleAttrValue;
import com.neu.gmall.service.SkuService;
import com.neu.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {

    @Reference
    private SkuService skuService;

    @Reference
    private SpuService spuService;

    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String skuId,ModelMap modelMap){

        PmsSkuInfo pmsSkuInfo = skuService.getSkuById(skuId);
        //sku对象
        modelMap.put("skuInfo",pmsSkuInfo);

        //销售属性列表:通过skuID查询该sku的spu所有属性列表，并通过ischecked表示该sku的属性值
        List<PmsProductSaleAttr> pmsProductSaleAttrList=spuService.spuSaleAttrListCheckBySku(pmsSkuInfo.getProductId(),pmsSkuInfo.getId());
        modelMap.put("spuSaleAttrListCheckBySku",pmsProductSaleAttrList);

        //查询当前sku所属spu的其余sku集合hash表
        List<PmsSkuInfo> pmsSkuInfoList=skuService.getSkuSaleAttrValueListBySpu(pmsSkuInfo.getProductId());
        Map<String,String > hashMap = new HashMap<String, String>();
        for(PmsSkuInfo p:pmsSkuInfoList){
            //"属性值1|属性值2……"
            String k = "";
            //v:skuId
            String v = String.valueOf(p.getId());
            List<PmsSkuSaleAttrValue> skuAttrValueList = p.getSkuSaleAttrValueList();
            for(PmsSkuSaleAttrValue sav:skuAttrValueList){
                k+=String.valueOf(sav.getSaleAttrValueId())+"|";
            }
            hashMap.put(k,v);
        }

        //将销售属性放到页面
        String skuSaleAttrHashJsonStr=JSON.toJSONString(hashMap);
        modelMap.put("skuSaleAttrHashJsonStr",skuSaleAttrHashJsonStr);


        return "item";
    }
}
