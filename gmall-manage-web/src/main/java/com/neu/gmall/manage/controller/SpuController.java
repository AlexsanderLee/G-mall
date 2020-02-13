package com.neu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.neu.gmall.bean.*;
import com.neu.gmall.service.SkuService;
import com.neu.gmall.service.SpuService;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@CrossOrigin
public class SpuController {
    @Reference
    private SpuService spuService;

    /**
     * 获取Spu列表
     * @param catalog3Id
     * @return
     */
    @RequestMapping("spuList")
    @ResponseBody
    public List<PmsProductInfo> spuList(String catalog3Id){
        List<PmsProductInfo> spuList = spuService.getSpuList(catalog3Id);
        return spuList;
    }
//
//    @RequestMapping("spuSaleAttrList")
//    @ResponseBody
//    public List<> spuSaleAttrList(String spuId){
//
//    }

    /**
     * 显示销售属性
     * @return
     */
    @RequestMapping("baseSaleAttrList")
    @ResponseBody
    public List<PmsBaseSaleAttr> baseSaleAttrList(){
        List<PmsBaseSaleAttr> pmsProductSaleAttrList = spuService.getSpuProductSaleAttr();
        return  pmsProductSaleAttrList;
    }

    /**
     * 保存Spu属性
     */
    @RequestMapping("saveSpuInfo")
    @ResponseBody
    public String  saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){
        spuService.saveSpuInfo(pmsProductInfo);
        return "success";
    }

    @Value("${fileServer.url}")
    String fileUrl;

    /**
     * fileUpload
     * 将http表单的文件类型转换
     */
    @RequestMapping(value = "fileUpload",method = RequestMethod.POST)
    @ResponseBody
    public String fileUpload(@RequestParam("file") MultipartFile file) throws IOException, MyException {
        //将图片文件上传到存储服务器

        //将图片存储地址返回给页面
        String imgUrl=fileUrl;
        if(file!=null){
            System.out.println("multipartFile = " + file.getName()+"|"+file.getSize());

            String configFile = this.getClass().getResource("/tracker.conf").getFile();
            ClientGlobal.init(configFile);
            TrackerClient trackerClient=new TrackerClient();
            TrackerServer trackerServer=trackerClient.getTrackerServer();
            StorageClient storageClient=new StorageClient(trackerServer,null);
            String filename=    file.getOriginalFilename();
            String extName = StringUtils.substringAfterLast(filename, ".");

            String[] upload_file = storageClient.upload_file(file.getBytes(), extName, null);
            imgUrl=fileUrl ;
            for (int i = 0; i < upload_file.length; i++) {
                String path = upload_file[i];
                imgUrl+="/"+path;
            }

        }

        return imgUrl;
    }

    @RequestMapping("spuSaleAttrList")
    @ResponseBody
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId){
        List<PmsProductSaleAttr> skuInfoList = spuService.getSpuSaleAttrList(spuId);
        return skuInfoList;
    }

    @RequestMapping("spuImageList")
    @ResponseBody
    public List<PmsProductImage> spuImageList(String spuId){
        List<PmsProductImage> list =  spuService.getSpuImageList(spuId);
        return list;
    }

}
