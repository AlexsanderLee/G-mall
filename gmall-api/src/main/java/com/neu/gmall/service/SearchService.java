package com.neu.gmall.service;

import com.neu.gmall.bean.PmsSearchParam;
import com.neu.gmall.bean.PmsSearchSkuInfo;

import java.util.List;

public interface SearchService {

    List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam);

}
