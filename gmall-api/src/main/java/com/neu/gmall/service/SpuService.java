package com.neu.gmall.service;

import com.neu.gmall.bean.PmsBaseSaleAttr;
import com.neu.gmall.bean.PmsProductImage;
import com.neu.gmall.bean.PmsProductInfo;
import com.neu.gmall.bean.PmsProductSaleAttr;

import java.util.List;

public interface SpuService {
    List<PmsProductInfo> getSpuList(String catalog3Id);

    List<PmsBaseSaleAttr> getSpuProductSaleAttr();

    void saveSpuInfo(PmsProductInfo pmsProductInfo);

    List<PmsProductSaleAttr> getSpuSaleAttrList(String spuId);

    List<PmsProductImage> getSpuImageList(String spuId);

    List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(Long productId, Long id);
}
