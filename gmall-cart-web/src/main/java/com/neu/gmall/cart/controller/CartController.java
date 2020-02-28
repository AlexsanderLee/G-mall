package com.neu.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.neu.gmall.annotations.LoginRequired;
import com.neu.gmall.bean.Constants;
import com.neu.gmall.bean.OmsCartItem;
import com.neu.gmall.bean.PmsSkuInfo;
import com.neu.gmall.service.OmsCartService;
import com.neu.gmall.service.SkuService;
import com.neu.gmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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

    @Reference
    private OmsCartService omsCartService;

    //交易界面
    @RequestMapping("toTrade")
    @LoginRequired(loginSuccess = true)
    public String toTrade(HttpServletResponse response, HttpServletRequest request, ModelMap modelMap){
        //使用强转，防止产生 null
        String memberId = (String)request.getAttribute(Constants.memberId);
        String nickname = (String)request.getAttribute(Constants.nickname);

        return "toTrade";
    }



    @RequestMapping("cartList")
    @LoginRequired(loginSuccess = false)
    public String cartList(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, ModelMap modelMap) {
        List<OmsCartItem> omsCartItemList = new ArrayList<>();
        String memberId = (String)httpServletRequest.getAttribute(Constants.memberId);

        if (StringUtils.isNotBlank(memberId)) {
            omsCartItemList = omsCartService.cartList(memberId);
        } else {
            //查询cookies
            String cartListCookie = CookieUtil.getCookieValue(httpServletRequest, "cartListCookie", true);
            if (StringUtils.isNotBlank(cartListCookie)) {
                omsCartItemList = JSON.parseArray(cartListCookie, OmsCartItem.class);
            }
        }

        modelMap.put("cartList", omsCartItemList);
//        //被勾选商品总额
//        BigDecimal totalAmount = getTotalPrice(omsCartItemList);
//        modelMap.put("totalAmount", totalAmount);
        return "cartList";
    }

    //购物车添加数量功能，需要修改前端页面
    @RequestMapping("checkCart")
    @LoginRequired(loginSuccess = false)
    public String checkCart(String isChecked,String skuId,HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, ModelMap modelMap) {
        String memberId = (String)httpServletRequest.getAttribute(Constants.memberId);

        //对购物车信息更新
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setIsChecked(isChecked);
        omsCartItem.setProductSkuId(Long.parseLong(skuId));
        omsCartItem.setMemberId(Long.parseLong(memberId));
        omsCartService.checkCart(omsCartItem);

        //将最新数据从缓存中取出
        List<OmsCartItem> omsCartItemList  = omsCartService.cartList(memberId);
        modelMap.put("cartList",omsCartItemList);

        //被勾选商品总额
        BigDecimal totalAmount = getTotalPrice(omsCartItemList);
        modelMap.put("totalAmount", totalAmount);
        //返回内嵌页
        return "cartListInner";
    }

    @RequestMapping("addToCart")
    @LoginRequired(loginSuccess = false)
    public String addToCart(String skuId, int quantity, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) {
        String memberId = (String)httpServletRequest.getAttribute(Constants.memberId);
        String nickName = (String)httpServletRequest.getAttribute(Constants.nickname);
        //购物车列表
        List<OmsCartItem> omsCartItemList = new ArrayList<>();
        //调用商品服务查询商品信息
        PmsSkuInfo skuById = skuService.getSkuById(skuId);

        Long quantity1=new Long(quantity);
        //将商品信息封装成购物车信息
        OmsCartItem omsCartItem = new OmsCartItem();
        buildCartItem(skuById, omsCartItem, quantity1);



        if (StringUtils.isNotBlank(memberId)) {
            //登录使用DB+Redis
            //用户登录，查询购物车数据
            OmsCartItem omsCartFromDB = omsCartService.ifExistInDB(memberId, skuId);
            if (omsCartFromDB != null) {
                omsCartFromDB.setQuantity(omsCartFromDB.getQuantity() + quantity1);
                //Double count = (double) omsCartFromDB.getQuantity();
                omsCartService.updateCart(omsCartFromDB);
            } else {
                omsCartItem.setMemberId(Long.parseLong(memberId));
                omsCartItem.setMemberNickname(nickName);
                omsCartService.addCart(omsCartItem);
            }
            // 同步缓存
            omsCartService.flushCartCache(memberId);
        } else {
            //未登录使用cookies
            //cookie原有数据
            String cartListCookie = CookieUtil.getCookieValue(httpServletRequest, "cartListCookie", true);
            //cookie不为空
            if (StringUtils.isNotBlank(cartListCookie)) {
                omsCartItemList = JSON.parseArray(cartListCookie, OmsCartItem.class);
                boolean exist = if_exist(omsCartItemList, omsCartItem);
                if (exist) {
                    for (OmsCartItem omsCartItem1 : omsCartItemList) {
                        if (omsCartItem1.getProductSkuId() == Long.parseLong(skuId)) {
                            omsCartItem.setQuantity(omsCartItem.getQuantity() + quantity1);
                            omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(new BigDecimal(omsCartItem.getQuantity())));
                        }
                    }
                } else {
                    omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(new BigDecimal(quantity1)));
                    omsCartItemList.add(omsCartItem);
                }

            }
            CookieUtil.setCookie(httpServletRequest, httpServletResponse, "cartListCookie", JSON.toJSONString(omsCartItemList), 60 * 60 * 72, true);


        }

        //重定向的静态页面
        return "redirect:/success.html";
    }

    private BigDecimal getTotalPrice(List<OmsCartItem> omsCartItemList) {
        BigDecimal totalPrice = new BigDecimal(0);
        for (OmsCartItem omsCartItem : omsCartItemList) {
            //被选中
            if (omsCartItem.getIsChecked().equals("1")) {
                totalPrice = totalPrice.add(omsCartItem.getTotalPrice());
            }
        }
        return totalPrice;
    }

    private boolean if_exist(List<OmsCartItem> omsCartItemList, OmsCartItem omsCartItem) {
        for (OmsCartItem omsCartItem1 : omsCartItemList) {
            //存在
            if (omsCartItem1.getProductSkuId() == omsCartItem.getProductSkuId()) {
                return true;
            }
        }
        return false;
    }

    private void buildCartItem(PmsSkuInfo skuInfo, OmsCartItem omsCartItem, Long quantity) {
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
