package com.zzh.mapper.es;

import com.alibaba.fastjson.JSON;
import com.zzh.pojo.News;
import com.zzh.tools.EsProperties;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class EsDaoImpl implements EsDao {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    @Override
    public SearchHit[] searchTitle(String index, String text, Integer from, Integer size) throws IOException {
        // 创建查询请求
        SearchRequest searchRequest = new SearchRequest(EsProperties.INDEX);
        // 构建查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 精确条件构建
        QueryBuilder query = QueryBuilders.matchQuery("title", text);
        searchSourceBuilder.query(query);

        // 高亮条件构建
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags(EsProperties.HIGHLIGHT_PRE_TAGS);
        highlightBuilder.postTags(EsProperties.HIGHLIGHT_POST_TAGS);
        highlightBuilder.numOfFragments(EsProperties.HIGHLIGHT_NUMBER_OF_FRAGMENTS);
        searchSourceBuilder.highlighter(highlightBuilder);

        // 查询数
        searchSourceBuilder.from(EsProperties.FROM);
        searchSourceBuilder.size(EsProperties.SIZE);
        searchSourceBuilder.fetchSource(new String[]{"newsId", "publicationDate", "title", "url"}, null);
        searchSourceBuilder.timeout(EsProperties.TERN_TIMEOUT);

        searchRequest.source(searchSourceBuilder);

        // 发送请求并获得响应
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println("dao" + searchResponse.getHits().getHits().length);
        return searchResponse.getHits().getHits();
    }

    @Override
    public SearchHit[] searchTitleAndContent(String index, String text, Integer from, Integer size) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        // 构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 高亮构建
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.field("content");
        highlightBuilder.preTags(EsProperties.HIGHLIGHT_PRE_TAGS);
        highlightBuilder.postTags(EsProperties.HIGHLIGHT_POST_TAGS);
        highlightBuilder.numOfFragments(EsProperties.HIGHLIGHT_NUMBER_OF_FRAGMENTS);
        searchSourceBuilder.highlighter(highlightBuilder);

        // 多条件匹配
        QueryBuilder query1 = QueryBuilders.matchQuery("title", text);
        QueryBuilder query2 = QueryBuilders.matchQuery("content", text);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().should(query1).should(query2);
        searchSourceBuilder.query(boolQueryBuilder);

        // 设置返回数
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchSourceBuilder.fetchSource(new String[]{"newsId","publicationDate","title","url"}, null);

        // 构建完成
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return searchResponse.getHits().getHits();
    }

    @Override
    public <T> Boolean bulk(String index, List<News> newses) throws IOException {
        // 批量传输
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout(EsProperties.BULK_TIMEOUT);

        for (News news : newses) {
            bulkRequest.add(new IndexRequest(index)
                    .id("" + news.getNewsId())
                    .source(JSON.toJSONString(news), XContentType.JSON));
        }

        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        return bulkResponse.hasFailures();
    }
}
