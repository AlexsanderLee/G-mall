package com.neu.gmall.search.controller;



import com.alibaba.dubbo.config.annotation.Reference;
import com.neu.gmall.bean.PmsSearchParam;
import com.neu.gmall.bean.PmsSearchSkuInfo;
import com.neu.gmall.service.SearchService;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class SearchController {

    @Reference
    private SearchService searchService;

    @RequestMapping("index.html")
    public String index(){
        return "index";
    }

    @RequestMapping("list.html")
    public String list(PmsSearchParam pmsSearchParam, ModelMap modelMap){
       List<PmsSearchSkuInfo> list = searchService.list(pmsSearchParam);
        modelMap.put("skuLsInfoList",list);
        return "list";
    }
}
