package com.zzh.service;

import com.zzh.mapper.NewsMapper;
import com.zzh.mapper.es.EsDaoImpl;
import com.zzh.pojo.News;
import com.zzh.pojo.NewsPart;
import com.zzh.pojo.NewsShowing;
import com.zzh.tools.EsProperties;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class EsService {
    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private EsDaoImpl esDao;

    public List<NewsPart> searchTitle(String text) throws IOException {
        // 封装
        List<NewsPart> newsParts = new ArrayList<>(EsProperties.SIZE);
        // es返回的数据类型
//        newsId->class java.lang.Integer
//        title->class java.lang.String
//        publicationDate->class java.lang.Long
//        url->class java.lang.String
        SearchHit[] hits = esDao.searchTitle(EsProperties.INDEX, text, EsProperties.FROM, EsProperties.SIZE);
        for (SearchHit hit : hits) {
            NewsPart newsPart = new NewsPart();
            Map<String, Object> source = hit.getSourceAsMap();
            newsPart.setNewsId((Integer) source.get("newsId"));
            newsPart.setPublicationDate(new Date((Long) source.get("publicationDate")));
            newsPart.setUrl((String) source.get("url"));

            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            Map<String, String> stringStringMap = parseHighLight(highlightFields);
            newsPart.setTitle(stringStringMap.get("title"));

            newsParts.add(newsPart);
        }
        return newsParts;
    }

    public List<NewsShowing> searchTitleAndContent(String text) throws IOException {
        List<NewsShowing> newsShowings = new ArrayList<>();

        SearchHit[] hits = esDao.searchTitleAndContent(EsProperties.INDEX, text, EsProperties.FROM, EsProperties.SIZE);
        for (SearchHit hit : hits) {
            NewsShowing newsShowing = new NewsShowing();
            Map<String, Object> source = hit.getSourceAsMap();
            newsShowing.setUrl((String) source.get("url"));
            newsShowing.setNewsId((Integer) source.get("newsId"));
            newsShowing.setPublicationDate(new Date((Long) source.get("publicationDate")));

            // 解析高亮部分
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            Map<String, String> stringStringMap = parseHighLight(highlightFields);
            newsShowing.setIntroduction(stringStringMap.get("content"));
            newsShowing.setTitle(stringStringMap.get("title"));

            newsShowings.add(newsShowing);
        }
        return newsShowings;
    }

    public Map<String, String> parseHighLight(Map<String, HighlightField> highlightFields) {
        HashMap<String, String> res = new HashMap<>(highlightFields.size());
        highlightFields.forEach((key, highlightField) -> {
            StringBuilder sb = new StringBuilder();
            Text[] fragments = highlightField.fragments();
            for (Text fragment : fragments) {
                sb.append(fragment.string());
                if ("content".equals(key))
                    sb.append("...");
            }
            res.put(key, sb.toString());
        });
        return res;
    }


    // 将mysql的news数据转到es
    public void mySqlToES() {
        int i = 0;
        List<News> newses = newsMapper.getNewsByPage(i++, 30);

        try {
            while (newses != null && newses.size() > 0) {
                if (i % 500 == 0)
                    System.out.println("=====================" + i + "=====================");
                Boolean bulk = esDao.bulk(EsProperties.INDEX, newses);
                if (bulk) {
                    System.out.println("=========================失败===========================" + i);
                    break;
                }
                newses = newsMapper.getNewsByPage(i++, 30);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
