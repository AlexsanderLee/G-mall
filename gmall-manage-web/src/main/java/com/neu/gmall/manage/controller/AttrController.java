package com.neu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.neu.gmall.bean.PmsBaseAttrInfo;
import com.neu.gmall.bean.PmsBaseAttrValue;
import com.neu.gmall.service.AttrService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@CrossOrigin
public class AttrController {
    @Reference
    private AttrService attrService;

    /**
     * 显示属性列表
     * @param catalog3Id
     * @return
     */
    @RequestMapping("attrInfoList")
    @ResponseBody
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id){
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrService.attrInfoList(catalog3Id);
        return pmsBaseAttrInfos;
    }
    /**
     * 平台属性添加功能:先查找属性
     */
    @RequestMapping("saveAttrInfo")
    @ResponseBody
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo){
        attrService.saveAttrInfo(pmsBaseAttrInfo);
        return "success";
    }

    /**
     * 修改属性值功能:先查询
     */
    @RequestMapping("getAttrValueList")
    @ResponseBody
    public  List<PmsBaseAttrValue> getAttrValueList(String attrId){
        List<PmsBaseAttrValue> list=attrService.getAttrValueList(attrId);
        return list;
    }
}
