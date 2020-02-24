package com.neu.gmall.user.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.neu.gmall.bean.Constants;
import com.neu.gmall.bean.UmsMember;
import com.neu.gmall.bean.UmsMemberReceiveAddress;
import com.neu.gmall.service.UserService;
import com.neu.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.neu.gmall.user.mapper.UserMapper;
import com.neu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;
//import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    public List<UmsMember> getAllUser(){
        return  userMapper.selectAll();
    }

    @Override
    public List<UmsMemberReceiveAddress> getUmsMemberReceiveAddressById(String memberId) {
        //外键
//        Example e = new Example(UmsMemberReceiveAddress.class);
//
//        e.createCriteria().andEqualTo("memberId",memberId);
//
//        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.selectByExample(e);

        // 方法二:通过对象中不为空的项进行查询
       UmsMemberReceiveAddress u = new UmsMemberReceiveAddress();
       u.setMemberId(Long.parseLong(memberId));
       List<UmsMemberReceiveAddress> umsMemberReceiveAddresses1 = umsMemberReceiveAddressMapper.select(u);

        return umsMemberReceiveAddresses1;
    }

    @Override
    public UmsMember login(UmsMember umsMember) {
        Jedis jedis = null;
        StringBuffer sb = new StringBuffer();
        sb.append(Constants.user);
        //redis数据结构要改，此为偷懒的方法
        sb.append(umsMember.getPassword());
        sb.append(Constants.info);
        try {
            jedis= redisUtil.getJedis();
            if(jedis!=null){
                String s = jedis.get(sb.toString());
                if(StringUtils.isNotBlank(s)){
                    //密码正确
                    UmsMember umsMember1 = JSON.parseObject(s, UmsMember.class);
                    //finally中语句会在return前执行
                    return umsMember1;
                }
            }
            //redis宕机
            //缓存没有，查询数据库
            //分布式锁
            UmsMember umsMember1 = loginFromDB(umsMember);
            if(umsMember1!=null){
                jedis.setex(sb.toString(),60*60*60, JSON.toJSONString(umsMember1));
                return umsMember1;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }

        return null;
    }

    @Override
    public void addUserToken(Long id, String token) {
        Jedis jedis = null;
        try{
            jedis = redisUtil.getJedis();
            StringBuffer sb = new StringBuffer();
            sb.append(Constants.user);
            sb.append(String.valueOf(id));
            sb.append(Constants.token);
            jedis.setex(sb.toString(),60*60*2,token);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }

    }

    private UmsMember loginFromDB(UmsMember umsMember) {
        Example e = new Example(UmsMember.class);
        e.createCriteria().andEqualTo("username",umsMember.getUsername()).andEqualTo("password",umsMember.getPassword());
        UmsMember umsMember1 = userMapper.selectOneByExample(e);
        return  umsMember1;
    }


}
