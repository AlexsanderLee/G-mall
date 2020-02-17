package com.neu.gmall.bean;

import tk.mybatis.mapper.util.StringUtil;

//常量类
public class Constants {
    private Constants(){}
    //redis：sku+skuid+info
    public static final String sku =  "sku:";
    public static final String info = ":info";
    public static  final String lock = ":lock";
}
