package com.neu.gmall.bean;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

public class PmsSkuInfo implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    private Long id;
    @Column
    private Long productId;
    @Column
    private Double price;
    @Column
    private String skuName;
    @Column
    private String skuDesc;
    @Column
    private Double weight;
    @Column
    private Long tmId;
    @Column
    private Long catalog3Id;
    @Column
    private String skuDefaultImg;


    @Transient
    List<PmsSkuImage> pmsSkuImageList;

    @Transient
    List<PmsSkuAttrValue> pmsSkuAttrValueList;

    @Transient
    List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValueList;

    public List<PmsSkuImage> getPmsSkuImageList() {
        return pmsSkuImageList;
    }

    public void setPmsSkuImageList(List<PmsSkuImage> pmsSkuImageList) {
        this.pmsSkuImageList = pmsSkuImageList;
    }

    public List<PmsSkuAttrValue> getPmsSkuAttrValueList() {
        return pmsSkuAttrValueList;
    }

    public void setPmsSkuAttrValueList(List<PmsSkuAttrValue> pmsSkuAttrValueList) {
        this.pmsSkuAttrValueList = pmsSkuAttrValueList;
    }

    public List<PmsSkuSaleAttrValue> getPmsSkuSaleAttrValueList() {
        return pmsSkuSaleAttrValueList;
    }

    public void setPmsSkuSaleAttrValueList(List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValueList) {
        this.pmsSkuSaleAttrValueList = pmsSkuSaleAttrValueList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName == null ? null : skuName.trim();
    }

    public String getSkuDesc() {
        return skuDesc;
    }

    public void setSkuDesc(String skuDesc) {
        this.skuDesc = skuDesc == null ? null : skuDesc.trim();
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Long getTmId() {
        return tmId;
    }

    public void setTmId(Long tmId) {
        this.tmId = tmId;
    }

    public Long getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(Long catalog3Id) {
        this.catalog3Id = catalog3Id;
    }

    public String getSkuDefaultImg() {
        return skuDefaultImg;
    }

    public void setSkuDefaultImg(String skuDefaultImg) {
        this.skuDefaultImg = skuDefaultImg == null ? null : skuDefaultImg.trim();
    }
}