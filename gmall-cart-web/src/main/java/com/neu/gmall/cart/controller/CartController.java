package com.neu.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.neu.gmall.bean.OmsCartItem;
import com.neu.gmall.bean.PmsSkuInfo;
import com.neu.gmall.service.SkuService;
import com.neu.gmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CartController {
    @Reference
    private SkuService skuService;

    @RequestMapping("addToCart")
    public String addToCart(String skuId, Long quantity, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest){
        List<OmsCartItem> omsCartItemList = new ArrayList<>();
        //调用商品服务查询商品信息
        PmsSkuInfo skuById = skuService.getSkuById(skuId);

        //将商品信息封装成购物车信息
        OmsCartItem omsCartItem = new OmsCartItem();
        buildCartItem(skuById,omsCartItem,quantity);

        //判断用户是否登录
        String memberId = "";
        if(StringUtils.isNotBlank(memberId)){
            //登录使用DB+Redis

        }else{
            //未登录使用cookies
            //cookie原有数据
            String cartListCookie = CookieUtil.getCookieValue(httpServletRequest, "cartListCookie", true);
            //cookie不为空
            if(StringUtils.isNotBlank(cartListCookie)) {
                omsCartItemList = JSON.parseArray(cartListCookie, OmsCartItem.class);
                boolean exist = if_exist(omsCartItemList, omsCartItem);
                if (exist) {
                    for(OmsCartItem omsCartItem1:omsCartItemList){
                        if(omsCartItem1.getProductSkuId()==Long.parseLong(skuId) ){
                            omsCartItem.setQuantity(omsCartItem.getQuantity()+quantity);
                        }
                    }
                } else {
                    omsCartItemList.add(omsCartItem);
                }
            }
            CookieUtil.setCookie(httpServletRequest,httpServletResponse,"cartListCookie", JSON.toJSONString(omsCartItemList),60 * 60 * 72, true);


        }

        //重定向的静态页面
        return "redirect:/success.html";
    }

    private boolean if_exist(List<OmsCartItem> omsCartItemList, OmsCartItem omsCartItem) {
        for(OmsCartItem omsCartItem1:omsCartItemList){
            //存在
            if(omsCartItem1.getProductSkuId()==omsCartItem.getProductSkuId()){
                return true;
            }
        }
        return false;
    }

    private void buildCartItem(PmsSkuInfo skuInfo,OmsCartItem omsCartItem, Long quantity) {
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setDeleteStatus(0);
        omsCartItem.setModifyDate(new Date());
        omsCartItem.setPrice(skuInfo.getPrice());
        omsCartItem.setProductAttr("");
        omsCartItem.setProductBrand("");
        omsCartItem.setProductCategoryId(skuInfo.getCatalog3Id());
        omsCartItem.setProductId(skuInfo.getProductId());
        omsCartItem.setProductName(skuInfo.getSkuName());
        omsCartItem.setProductPic(skuInfo.getSkuDefaultImg());
        omsCartItem.setProductSkuCode("11111111111");
        omsCartItem.setProductSkuId(skuInfo.getId());
        omsCartItem.setQuantity(quantity);
    }


}
