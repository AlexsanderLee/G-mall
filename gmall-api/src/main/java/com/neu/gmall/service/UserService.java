package com.neu.gmall.service;


import com.neu.gmall.bean.UmsMember;
import com.neu.gmall.bean.UmsMemberReceiveAddress;

import java.util.List;

public interface UserService {
   List<UmsMember> getAllUser();

   List<UmsMemberReceiveAddress> getUmsMemberReceiveAddressById(String memberId);

    UmsMember login(UmsMember umsMember);

    void addUserToken(Long id, String token);

    List<UmsMemberReceiveAddress> getAddressById(String memberId);
}
