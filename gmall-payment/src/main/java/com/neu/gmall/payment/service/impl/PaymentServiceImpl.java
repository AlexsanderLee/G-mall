package com.neu.gmall.payment.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.neu.gmall.bean.PaymentInfo;
import com.neu.gmall.payment.mapper.PaymentInfoMapper;
import com.neu.gmall.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

@Service
public class PaymentServiceImpl  implements PaymentService {

    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

    @Override
    public void savePaymentInfo(PaymentInfo paymentInfo) {
        paymentInfoMapper.insertSelective(paymentInfo);
    }

    @Override
    public void updatePayment(PaymentInfo paymentInfo) {
        Example e = new Example(PaymentInfo.class);
        e.createCriteria().andEqualTo("orderSn",paymentInfo.getOrderSn());
        paymentInfoMapper.updateByExampleSelective(paymentInfo,e);
    }
}
