package com.neu.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.neu.gmall.annotations.LoginRequired;
import com.neu.gmall.bean.Constants;

import com.neu.gmall.bean.OmsCartItem;
import com.neu.gmall.bean.OmsOrderItem;
import com.neu.gmall.bean.UmsMemberReceiveAddress;
import com.neu.gmall.service.OmsCartService;
import com.neu.gmall.service.OrderService;
import com.neu.gmall.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class OrderController {

    @Reference
    private OrderService orderService;

    @Reference
    private UserService userService;

    @Reference
    private OmsCartService cartService;

    @RequestMapping("submitOrder")
    @LoginRequired(loginSuccess = true)
    public String submitOrder(String receiveAddressId, BigDecimal totalAmount, String tradeCode, HttpServletRequest request,  ModelMap modelMap){

        String memberId = (String)request.getAttribute("memberId");
        String nickname = (String)request.getAttribute("nickname");

        //验证交易码
        boolean b = orderService.checkTradeCode(memberId,tradeCode);
        if(b){
            //查询购买商品列表信息

            //检价

            //验库存

            //存入订单信息

            //删除购物车物品

            //重定向到支付系统

            return "";
        }else{
            return  "tradeFail";
        }



    }


    @RequestMapping("toTrade")
    @LoginRequired(loginSuccess = true)
    public  String toTrade(HttpServletRequest request, ModelMap modelMap){
        String memberId = (String)request.getAttribute(Constants.memberId);
        String nickname = (String)request.getAttribute(Constants.nickname);

        //收货地址
        List<UmsMemberReceiveAddress> addressList = null;
        addressList = userService.getAddressById(memberId);
        //获取购物车列表
        List<OmsCartItem> cartItemList = cartService.cartList(memberId);
        //将购物车列表转化为订单列表
        List<OmsOrderItem> orderItemList = new ArrayList<>();
        for(OmsCartItem cartItem:cartItemList){
            //购物车选中
            if(cartItem.getIsChecked().equals("1")){
                OmsOrderItem item = new OmsOrderItem();
                item.setProductPic(cartItem.getProductPic());
                item.setProductName(cartItem.getProductName());
                orderItemList.add(item);
            }
        }

        BigDecimal totalAmount = getTotalPrice(cartItemList);


        modelMap.put("userAddressList",addressList);
        modelMap.put("omsOrderItems",orderItemList);
        modelMap.put("totalAmount",totalAmount);

        //生成交易码，进行提交订单时的校验
        String tradeCode = orderService.genTradeCode(memberId);
        modelMap.put("tradeCode",tradeCode);

        return "trade";
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

}
