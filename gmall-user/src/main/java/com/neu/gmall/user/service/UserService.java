package com.neu.gmall.user.service;

import com.neu.gmall.user.bean.UmsMember;
import com.neu.gmall.user.bean.UmsMemberReceiveAddress;

import java.util.List;

public interface UserService {
   List<UmsMember> getAllUser();

   List<UmsMemberReceiveAddress> getUmsMemberReceiveAddressById(String memberId);
}
