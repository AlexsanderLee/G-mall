package com.neu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.neu.gmall.bean.*;
import com.neu.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@CrossOrigin
public class SpuController {
    @Reference
    private SpuService spuService;

    /**
     * 获取Spu列表
     * @param catalog3Id
     * @return
     */
    @RequestMapping("spuList")
    @ResponseBody
    public List<PmsProductInfo> spuList(String catalog3Id){
        List<PmsProductInfo> spuList = spuService.getSpuList(catalog3Id);
        return spuList;
    }
//
//    @RequestMapping("spuSaleAttrList")
//    @ResponseBody
//    public List<> spuSaleAttrList(String spuId){
//
//    }

    /**
     * 显示销售属性
     * @return
     */
    @RequestMapping("baseSaleAttrList")
    @ResponseBody
    public List<PmsBaseSaleAttr> baseSaleAttrList(){
        List<PmsBaseSaleAttr> pmsProductSaleAttrList = spuService.getSpuProductSaleAttr();
        return  pmsProductSaleAttrList;
    }

    /**
     * 保存Spu属性
     */
    @RequestMapping("saveSpuInfo")
    @ResponseBody
    public String  saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){
        spuService.saveSpuInfo(pmsProductInfo);
        return "success";
    }

}
