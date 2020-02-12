package com.neu.gmall.service;

import com.neu.gmall.bean.PmsBaseSaleAttr;
import com.neu.gmall.bean.PmsProductInfo;

import java.util.List;

public interface SpuService {
    List<PmsProductInfo> getSpuList(String catalog3Id);

    List<PmsBaseSaleAttr> getSpuProductSaleAttr();

    void saveSpuInfo(PmsProductInfo pmsProductInfo);
}
