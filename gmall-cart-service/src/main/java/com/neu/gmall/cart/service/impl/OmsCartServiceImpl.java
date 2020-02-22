package com.neu.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.neu.gmall.bean.Constants;
import com.neu.gmall.bean.OmsCartItem;
import com.neu.gmall.cart.mapper.OmsCartItemMapper;
import com.neu.gmall.service.OmsCartService;
import com.neu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OmsCartServiceImpl implements OmsCartService {

    @Autowired
    private OmsCartItemMapper omsCartItemMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public OmsCartItem ifExistInDB(String memberId, String skuId) {
        Example e = new Example(OmsCartItem.class);
        e.createCriteria().andEqualTo("memberId",memberId).andEqualTo("productSkuId",skuId);
        OmsCartItem omsCartItem = omsCartItemMapper.selectOneByExample(e);

        return omsCartItem;
    }

    //添加购物车
    @Override
    public void addCart(OmsCartItem omsCartItem) {
        if(omsCartItem.getMemberId()!=null)
            omsCartItemMapper.insertSelective(omsCartItem);
    }

    @Override
    public void flushCartCache(String memberId) {
        OmsCartItem omsCartItem = new OmsCartItem();
        Long mId = Long.parseLong(memberId);
        omsCartItem.setMemberId(mId);
        List<OmsCartItem> cartItems = omsCartItemMapper.select(omsCartItem);
        //同步到缓存
        Jedis jedis = redisUtil.getJedis();
        Map<String,String > map = new HashMap<>();
        for(OmsCartItem cartItem:cartItems){
            cartItem.setTotalPrice(cartItem.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
            map.put(String.valueOf(cartItem.getProductSkuId()), JSON.toJSONString(cartItem));
        }
        jedis.del(Constants.user+memberId+Constants.cart);
        jedis.hmset(Constants.user+memberId+Constants.cart,map);
        jedis.close();
    }

    @Override
    public void updateCart(OmsCartItem omsCartFromDB) {
        Example e = new Example(OmsCartItem.class);
        e.createCriteria().andEqualTo("id",omsCartFromDB.getId());
        omsCartItemMapper.updateByExampleSelective(omsCartFromDB,e);
    }

    //redis读取数据
    @Override
    public List<OmsCartItem> cartList(String memberId) {
        Jedis jedis=null;
        List<OmsCartItem>  omsCartItemList = new ArrayList<>();
        try{
            jedis = redisUtil.getJedis();
            List<String> hvals = jedis.hvals(Constants.user + memberId + Constants.cart);
            for(String s: hvals){
                omsCartItemList.add(JSON.parseObject(s,OmsCartItem.class));
            }

        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }finally {
            jedis.close();
        }


        return omsCartItemList;
    }

    @Override
    public void checkCart(OmsCartItem omsCartItem) {
        Example e = new Example(OmsCartItem.class);
        //getProductSkuId
        e.createCriteria().andEqualTo("memberId",omsCartItem.getMemberId()).andEqualTo("productSkuId",omsCartItem.getProductSkuId());
        omsCartItemMapper.updateByExampleSelective(omsCartItem,e);
        //刷新缓存
        flushCartCache(String.valueOf(omsCartItem.getMemberId()));
    }
}
