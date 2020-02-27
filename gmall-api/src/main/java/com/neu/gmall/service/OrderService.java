package com.neu.gmall.service;

public interface OrderService {
    String genTradeCode(String memberId);

    boolean checkTradeCode(String memberId, String tradeCode);
}
