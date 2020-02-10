package com.neu.gmall.user.mapper;


import com.neu.gmall.user.bean.UmsMember;
import tk.mybatis.mapper.common.Mapper;

//使用通用mapper解决单表的增删改查
public interface UserMapper extends Mapper<UmsMember> {

}
