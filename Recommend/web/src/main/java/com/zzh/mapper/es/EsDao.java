package com.zzh.mapper.es;

import com.zzh.pojo.News;
import org.elasticsearch.search.SearchHit;

import java.io.IOException;
import java.util.List;

public interface EsDao {
    // es操作api
    public SearchHit[] searchTitle(String index, String text, Integer from, Integer size) throws IOException;

    public SearchHit[] searchTitleAndContent(String index, String text, Integer from, Integer size) throws IOException;

    public <T> Boolean bulk(String index, List<News> newses) throws IOException;
}

