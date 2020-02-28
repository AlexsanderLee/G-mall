package com.neu.gmall.service;

import com.neu.gmall.bean.PmsProductSaleAttr;
import com.neu.gmall.bean.PmsSkuInfo;

import java.math.BigDecimal;
import java.util.List;

public interface SkuService {


    void saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    PmsSkuInfo getSkuById(String skuId);

    List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(Long productId);

    List<PmsSkuInfo> getAllSku();

    boolean checkPrice(Long productSkuId, BigDecimal price);
}
