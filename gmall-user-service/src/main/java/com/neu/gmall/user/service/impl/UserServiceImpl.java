package com.neu.gmall.user.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.neu.gmall.bean.UmsMember;
import com.neu.gmall.bean.UmsMemberReceiveAddress;
import com.neu.gmall.service.UserService;
import com.neu.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.neu.gmall.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

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


}
