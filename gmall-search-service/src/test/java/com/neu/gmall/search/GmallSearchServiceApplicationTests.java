package com.neu.gmall.search;


import com.alibaba.dubbo.config.annotation.Reference;

import com.neu.gmall.bean.Constants;
import com.neu.gmall.bean.PmsSearchSkuInfo;
import com.neu.gmall.bean.PmsSkuInfo;
import com.neu.gmall.service.SkuService;
import io.searchbox.client.JestClient;

import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import jodd.io.findfile.FindFile;
import org.elasticsearch.common.recycler.Recycler;
import org.elasticsearch.index.engine.Engine;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.elasticsearch.jest.JestAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallSearchServiceApplicationTests {

    @Reference
    SkuService skuService;


    @Autowired
    JestClient jestClient;


    @Test
    public  void contextLoads() throws IOException {
        List<PmsSearchSkuInfo> pmsSearchSkuInfoList = new ArrayList<>();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //bool
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        //filter
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("","");
        TermsQueryBuilder termsQueryBuilder = new TermsQueryBuilder("","");
        boolQueryBuilder.filter( termQueryBuilder);
        //must
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("","");
        boolQueryBuilder.must(matchQueryBuilder);

        //query
        searchSourceBuilder.query(boolQueryBuilder);
        //from
        searchSourceBuilder.from(0);
        //size
        searchSourceBuilder.size(20);
        //highlight
        searchSourceBuilder.highlight(null);

        //使用复杂api进行查询
        //原理是使用封装后的dsl语句
        Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(Constants.index).addType(Constants.type).build();
        SearchResult result = jestClient.execute(search);
        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = result.getHits(PmsSearchSkuInfo.class);
        for(SearchResult.Hit<PmsSearchSkuInfo, Void> hit:hits){
            PmsSearchSkuInfo pmsSearchSkuInfo = hit.source;
            pmsSearchSkuInfoList.add(pmsSearchSkuInfo);
        }

    }

    @Test
    //将数据库数据转化为es数据
    //这步很重要：需要保证PmsSearchSkuInfo与pmsSkuInfo对应数据结构、名称完全一致
    public void put() throws IOException {
        List<PmsSkuInfo> pmsSkuInfoList =skuService.getAllSku();
        //转化为es数据结构
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
        for (PmsSkuInfo pmsSkuInfo:
                pmsSkuInfoList) {
            PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();
            pmsSearchSkuInfo.setId(pmsSkuInfo.getId());
            BeanUtils.copyProperties(pmsSkuInfo,pmsSearchSkuInfo);
            pmsSearchSkuInfos.add(pmsSearchSkuInfo);
        }
        //导入es
        //int i=0;
        for (PmsSearchSkuInfo pmsSearchSkuInfo :pmsSearchSkuInfos) {
            Index put = new Index.Builder(pmsSearchSkuInfo).index(Constants.index).type(Constants.type).id(pmsSearchSkuInfo.getId()+"").build();
            jestClient.execute(put);
        }
    }

}
