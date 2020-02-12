package com.neu.gmall.service;

import com.neu.gmall.bean.PmsBaseAttrInfo;
import com.neu.gmall.bean.PmsBaseAttrValue;

import java.util.List;

public interface AttrService {

    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id);

    void saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    List<PmsBaseAttrValue> getAttrValueList(String attrId);
}
