package com.neu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.neu.gmall.bean.Constants;
import com.neu.gmall.bean.*;
import com.neu.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.neu.gmall.manage.mapper.PmsSkuImageMapper;
import com.neu.gmall.manage.mapper.PmsSkuInfoMapper;
import com.neu.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.neu.gmall.service.SkuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import com.neu.gmall.util.RedisUtil;


import java.util.List;
import java.util.UUID;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    private PmsSkuImageMapper pmsSkuImageMapper;

    @Autowired
    private PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    private PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo){
        //存储sku_info
        pmsSkuInfo.setProductId(Long.parseLong(pmsSkuInfo.getSpuId()));
        pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
        //存储图片信息
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for(PmsSkuImage pmsSkuImage:skuImageList){
            pmsSkuImage.setSkuId(pmsSkuInfo.getId());
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }

        for(PmsSkuAttrValue pmsSkuAttrValue:skuAttrValueList){
            pmsSkuAttrValue.setSkuId(pmsSkuInfo.getId());
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }

        for(PmsSkuSaleAttrValue pmsSkuSaleAttrValue:skuSaleAttrValueList){
            pmsSkuSaleAttrValue.setSkuId(pmsSkuInfo.getId());
            pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }

    }

    //不采用缓存
    public PmsSkuInfo getSkuByIdFromDB(String skuId){
        PmsSkuInfo skuInfoList=null;
        Long id =Long.parseLong(skuId);
        try {
            skuInfoList = pmsSkuInfoMapper.selectByPrimaryKey(id);
            PmsSkuImage pmsSkuImage = new PmsSkuImage();
            pmsSkuImage.setSkuId(id);
            List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.select(pmsSkuImage);
            skuInfoList.setSkuImageList(pmsSkuImages);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return skuInfoList;
    }

    /***
     * 基本思路
     @Override
     public PmsSkuInfo getSkuById(String skuId){
     PmsSkuInfo skuInfo=null;
     Long id =Long.parseLong(skuId);

     //连接缓存
     Jedis jedis = redisUtil.getJedis();
     //查询缓存
     String skuKey = Constants.sku + skuId + Constants.info;
     String  skuJson = jedis.get(skuKey);

     if(StringUtils.isNotBlank(skuJson)){
     skuInfo = JSON.parseObject(skuJson,PmsSkuInfo.class);

     }else{
     PmsSkuInfo skuByIdFromDB = getSkuByIdFromDB(skuId);
     //保存到redis
     if(skuByIdFromDB!=null){
     jedis.set(skuKey, JSON.toJSONString(skuByIdFromDB));

     }
     }
     jedis.close();

     return skuInfo;
     }
     ***/

    // redis设置分布式锁
    @Override
    public PmsSkuInfo getSkuById(String skuId)  {
        PmsSkuInfo skuInfo=null;
        Long id =Long.parseLong(skuId);
        //链接缓存
        Jedis jedis = redisUtil.getJedis();
        //查询缓存
        String key = Constants.sku + skuId + Constants.info;
        String skuJson = jedis.get(key);
        if(StringUtils.isBlank(skuJson)){
            //设置分布式锁:锁key,防止高并发访问
            String lockKey = Constants.sku+skuId+Constants.lock;
            String token = UUID.randomUUID().toString();
            String OK = jedis.set(lockKey, token,"nx","px",1000*10);
            //成功加锁
            if(StringUtils.isNotBlank(OK) && OK.equals("OK")){
                //访问数据库
                PmsSkuInfo skuByIdFromDB = getSkuByIdFromDB(skuId);
                if(skuByIdFromDB!=null){
                    jedis.set(key, JSON.toJSONString(skuByIdFromDB));
                }else{
                    //防止穿透,为查找到设置空串存储
                    jedis.setex(key,60*3,JSON.toJSONString(""));
                }
                //访问DB后释放锁
                jedis.del(lockKey);
            }else{
                //自旋，睡眠一段时间
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //return不会创建新的线程
                return getSkuById(skuId);
            }
        }else{

            skuInfo = JSON.parseObject(skuJson,PmsSkuInfo.class);
        }
        jedis.close();
        return skuInfo;
    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(Long productId){
        List<PmsSkuInfo> pmsSkuInfoList=pmsSkuInfoMapper.selectSkuSaleAttrValueListBySpu(productId);
        return pmsSkuInfoList;
    }

    @Override
    public List<PmsSkuInfo> getAllSku() {
        List<PmsSkuInfo> pmsSkuInfoList = pmsSkuInfoMapper.selectAll();
        for(PmsSkuInfo pmsSkuInfo:pmsSkuInfoList){
            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(pmsSkuInfo.getId());
            List<PmsSkuAttrValue> select = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);
            pmsSkuInfo.setSkuAttrValueList(select);

        }
        return pmsSkuInfoList;
    }


}
