package com.neu.gmall.search.controller;



import com.alibaba.dubbo.config.annotation.Reference;
import com.neu.gmall.bean.*;
import com.neu.gmall.service.AttrService;
import com.neu.gmall.service.SearchService;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
public class SearchController {

    @Reference
    private SearchService searchService;

    @Reference
    private AttrService attrService;

    @RequestMapping("index.html")
    public String index(){
        return "index";
    }

    @RequestMapping("list.html")
    public String list(PmsSearchParam pmsSearchParam, ModelMap modelMap){
        //根据查询参数查询sku集合
        List<PmsSearchSkuInfo> list = searchService.list(pmsSearchParam);
        modelMap.put("skuLsInfoList",list);

        //抽取检索结果包含的平台属性
        Set<Long> valueIdSet = new HashSet<>();
        for(PmsSearchSkuInfo pmsSearchSkuInfo:list){
            List<PmsSkuAttrValue> skuAttrValueList = pmsSearchSkuInfo.getSkuAttrValueList();
            for(PmsSkuAttrValue pmsSkuAttrValue:skuAttrValueList){
                valueIdSet.add(pmsSkuAttrValue.getValueId());
            }
        }

        //根据属性ID查询属性列表
        List<PmsBaseAttrInfo> pmsBaseAttrInfoList=attrService.getAttrValueListByValueId(valueIdSet);
        modelMap.put("attrList",pmsBaseAttrInfoList);

        //每选择一个属性删除当前属性所在的属性组
        //迭代器动态检查删除
        Long[] valueId = pmsSearchParam.getValueId();


        if(valueId!=null){
            //面包屑
            List<SearchCrumb> crumbs = new ArrayList<>();
            //valueId为当前页面包含的属性值，面包屑中url需要删除该属性值
            for(int i=0;i<valueId.length;i++){
                Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfoList.iterator();
                SearchCrumb searchCrumb = new SearchCrumb();
                searchCrumb.setValueId(valueId[i]);
                searchCrumb.setUrlParam(getUrlParam(pmsSearchParam,String.valueOf(valueId[i])));
                while (iterator.hasNext()){
                    PmsBaseAttrInfo next = iterator.next();

                    List<PmsBaseAttrValue> attrValueList = next.getAttrValueList();
                    for(PmsBaseAttrValue pmsBaseAttrValue:attrValueList){
                        Long attrId = pmsBaseAttrValue.getId();

                            if (valueId[i]==attrId){

                                searchCrumb.setValueName(pmsBaseAttrValue.getValueName());

                                iterator.remove();
                            }
                        }
                    }

                crumbs.add(searchCrumb);
                }
            //筛选条件，面包屑
            modelMap.put("attrValueSelectedList",crumbs);
        }


        String urlParam = getUrlParam(pmsSearchParam);
        modelMap.put("urlParam",urlParam);

        return "list";
    }

    String getUrlParam(PmsSearchParam pmsSearchParam,String ...delValueId){
        StringBuffer sb = new StringBuffer();
        String catalog3Id  = null;
        if(pmsSearchParam.getCatalog3Id()!=null)
            catalog3Id = String.valueOf(pmsSearchParam.getCatalog3Id());
        String keyword = pmsSearchParam.getKeyword();
        Long[] valueId = pmsSearchParam.getValueId();
        String[] valueIds = null;
        if(valueId!=null){
            valueIds = new String[valueId.length];
            for(int i=0;i<valueId.length;i++){
                valueIds[i]=String.valueOf(valueId[i]);
            }
        }
        if (catalog3Id != null && StringUtils.isNotBlank(catalog3Id)) {
            if(sb.length()!=0){
                sb.append('&');
            }
            sb.append("catalog3Id=");
            sb.append(catalog3Id);
        }
        if(StringUtils.isNotBlank(keyword)){
            if(sb.length()!=0){
                sb.append('&');
            }
            sb.append("keyword=");
            sb.append(keyword);
        }
        if(valueIds!=null){
            for(int i=0;i<valueIds.length;i++){
                //面包屑中删除自己的属性值,变参注意事项
                if(delValueId.length > 0 && delValueId[0].equals(valueIds[i])){
                    continue;
                }
                if(sb.length()!=0){
                    sb.append('&');
                }
                sb.append("valueId=");
                sb.append(valueIds[i]);
            }
        }
        return sb.toString();
    }
}
